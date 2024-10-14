package lifegame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class Observable<T> {
    private List<Consumer<T>> observers = new ArrayList<>();
    private List<Runnable> onCompleteActions = new ArrayList<>();
    private boolean completed = false;

    public static <T> Observable<T> create(Consumer<Emitter<T>> emitterConsumer) {
        Observable<T> observable = new Observable<>();
        Emitter<T> emitter = new Emitter<>(observable);
        emitterConsumer.accept(emitter);
        return observable;
    }

    public Observable<T> subscribe(Consumer<T> onNext, Runnable onComplete) {
        if (!completed) {
            observers.add(onNext);
            onCompleteActions.add(onComplete);
        } else {
            onComplete.run();
        }
        return this;
    }

    public Observable<T> subscribe(Consumer<T> onNext) {
        return subscribe(onNext, () -> {});
    }

    private void next(T value) {
        if (!completed) {
            for (Consumer<T> observer : observers) {
                observer.accept(value);
            }
        }
    }

    private void complete() {
        if (!completed) {
            completed = true;
            for (Runnable action : onCompleteActions) {
                action.run();
            }
            observers.clear();
            onCompleteActions.clear();
        }
    }

    public <R> Observable<R> switchMap(Function<T, Observable<R>> mapper) {
        Observable<R> result = new Observable<>();
        AtomicReference<Observable<R>> currentObservable = new AtomicReference<>();

        subscribe(value -> {
            Observable<R> newObservable = mapper.apply(value);

            Observable<R> previousObservable = currentObservable.getAndSet(newObservable);
            if (previousObservable != null) {
                previousObservable.complete();
            }

            newObservable.subscribe(result::next);
        });

        return result;
    }

    public Observable<T> merge(Observable<T> other) {
        Observable<T> result = new Observable<>();
        subscribe(result::next, result::complete);
        other.subscribe(result::next, result::complete);
        return result;
    }

    public Observable<T> takeUntil(Observable<?> trigger) {
        Observable<T> result = new Observable<>();
        subscribe(result::next, result::complete);
        trigger.subscribe(value -> result.complete());
        return result;
    }

    public Observable<T> distinctUntilChanged() {
        Observable<T> result = new Observable<>();
        AtomicReference<T> lastValue = new AtomicReference<>();

        subscribe(value -> {
            T previousValue = lastValue.get();
            if (previousValue == null || !previousValue.equals(value)) {
                lastValue.set(value);
                result.next(value);
            }
        });

        return result;
    }

    public <R> Observable<R> map(Function<T, R> mapper) {
        Observable<R> result = new Observable<>();
        subscribe(value -> result.next(mapper.apply(value)), result::complete);
        return result;
    }

    public Observable<T> filter(Function<T, Boolean> predicate) {
        Observable<T> result = new Observable<>();
        subscribe(value -> {
            if (predicate.apply(value)) {
                result.next(value);
            }
        }, result::complete);
        return result;
    }

    public static class Emitter<T> {
        private final Observable<T> observable;

        public Emitter(Observable<T> observable) {
            this.observable = observable;
        }

        public void next(T value) {
            observable.next(value);
        }

        public void complete() {
            observable.complete();
        }
    }
}