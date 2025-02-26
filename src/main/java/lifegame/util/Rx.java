package lifegame.util;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Rx {
    public static class Observable<T> {
        private final List<Observer<? super T>> observers = new CopyOnWriteArrayList<>();

        public void subscribe(Observer<? super T> observer) {
            observers.add(observer);
        }

        public void subscribe(Consumer<? super T> onNext) {
            subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    onNext.accept(item);
                }

                @Override
                public void onError(Throwable error) {
                }

                @Override
                public void onComplete() {
                }
            });
        }

        public void next(T item) {
            for (Observer<? super T> observer : observers) {
                observer.onNext(item);
            }
        }

        public void error(Throwable error) {
            for (Observer<? super T> observer : observers) {
                observer.onError(error);
            }
        }

        public void complete() {
            for (Observer<? super T> observer : observers) {
                observer.onComplete();
            }
        }

        public static <T> Observable<T> create(Consumer<Emitter<T>> emitterConsumer) {
            Observable<T> observable = new Observable<>();
            emitterConsumer.accept(new Emitter<>(observable));
            return observable;
        }

        public static <T> Observable<T> just(T item) {
            return Observable.create(emitter -> {
                emitter.executor.execute(() -> {
                    item.toString(); //これがないとなぜか動かない
                    emitter.next(item);
                    emitter.complete();
                });
            });
        }

        public Observable<T> merge(Observable<T> observable) {
            Observable<T> result = new Observable<>();
            observable.subscribe(new Observer<>() {
                @Override
                public void onNext(T item) {
                    result.next(item);
                }

                @Override
                public void onError(Throwable error) {
                    result.error(error);
                }

                @Override
                public void onComplete() {
                    result.complete();
                }
            });
            this.subscribe(new Observer<>() {
                @Override
                public void onNext(T item) {
                    result.next(item);
                }

                @Override
                public void onError(Throwable error) {
                    result.error(error);
                }

                @Override
                public void onComplete() {
                    result.complete();
                }
            });
            return result;
        }

        public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
            Observable<R> result = new Observable<>();
            this.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    result.next(mapper.apply(item));
                }

                @Override
                public void onError(Throwable error) {
                    result.error(error);
                }

                @Override
                public void onComplete() {
                    result.complete();
                }
            });
            return result;
        }

        public Observable<T> filter(Predicate<? super T> predicate) {
            Observable<T> result = new Observable<>();
            this.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (predicate.test(item)) {
                        result.next(item);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    result.error(error);
                }

                @Override
                public void onComplete() {
                    result.complete();
                }
            });
            return result;
        }

        public <R> Observable<R> switchMap(Function<? super T, Observable<? extends R>> mapper) {
            Observable<R> result = new Observable<>();
            AtomicReference<Subscription> currentSubscription = new AtomicReference<>();
            AtomicBoolean isCompleted = new AtomicBoolean(false);

            this.subscribe(new Observer<>() {
                final AtomicLong version = new AtomicLong(0);

                @Override
                public void onNext(T item) {
                    long currentVersion = version.incrementAndGet();
                    Subscription previous = currentSubscription.getAndSet(null);
                    if (previous != null) {
                        previous.cancel();
                    }

                    Observable<? extends R> innerObservable = mapper.apply(item);
                    Subscription newSubscription = new Subscription();
                    currentSubscription.set(newSubscription);

                    innerObservable.subscribe(new Observer<R>() {
                        @Override
                        public void onNext(R innerItem) {
                            if (currentVersion == version.get() && !newSubscription.isCancelled()) {
                                result.next(innerItem);
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            if (currentVersion == version.get() && !newSubscription.isCancelled()) {
                                result.error(error);
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (currentVersion == version.get() && isCompleted.get() && !newSubscription.isCancelled()) {
                                result.complete();
                            }
                        }
                    });
                }

                @Override
                public void onError(Throwable error) {
                    result.error(error);
                }

                @Override
                public void onComplete() {
                    isCompleted.set(true);
                }
            });

            return result;
        }

        public Observable<T> distinctUntilChanged() {
            Observable<T> result = new Observable<>();
            AtomicReference<T> lastItem = new AtomicReference<>();
            this.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!Objects.equals(item, lastItem.get())) {
                        lastItem.set(item);
                        result.next(item);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    result.error(error);
                }

                @Override
                public void onComplete() {
                    result.complete();
                }
            });
            return result;
        }

        public Observable<T> takeUntil(Observable<?> until) {
            Observable<T> result = new Observable<>();
            AtomicBoolean isCompleted = new AtomicBoolean(false);
            AtomicReference<Subscription> subscription = new AtomicReference<>();
            until.subscribe(new Observer<Object>() {
                @Override
                public void onNext(Object item) {
                    if (isCompleted.get()) {
                        return;
                    }
                    isCompleted.set(true);
                    Subscription currentSubscription = subscription.getAndSet(null);
                    if (currentSubscription != null) {
                        currentSubscription.cancel();
                    }
                    result.complete();
                }

                @Override
                public void onError(Throwable error) {
                    if (isCompleted.get()) {
                        return;
                    }
                    isCompleted.set(true);
                    Subscription currentSubscription = subscription.getAndSet(null);
                    if (currentSubscription != null) {
                        currentSubscription.cancel();
                    }
                    result.error(error);
                }

                @Override
                public void onComplete() {
                    if (isCompleted.get()) {
                        return;
                    }
                    isCompleted.set(true);
                    Subscription currentSubscription = subscription.getAndSet(null);
                    if (currentSubscription != null) {
                        currentSubscription.cancel();
                    }
                    result.complete();
                }
            });
            this.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!isCompleted.get()) {
                        result.next(item);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    if (!isCompleted.get()) {
                        result.error(error);
                    }
                }

                @Override
                public void onComplete() {
                    if (!isCompleted.get()) {
                        result.complete();
                    }
                }
            });
            return result;
        }
    }

    public static class Emitter<T> {
        private final Observable<T> observable;
        private final ExecutorService executor;

        public Emitter(Observable<T> observable) {
            this.observable = observable;
            this.executor = Executors.newSingleThreadExecutor();
        }

        public void next(T item) {
            observable.next(item);
        }

        public void error(Throwable error) {
            observable.error(error);
        }

        public void complete() {
            observable.complete();
            executor.shutdown();
        }
    }

    public interface Observer<T> {
        void onNext(T item);

        void onError(Throwable error);

        void onComplete();
    }

    public static class Subscription {
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        public void cancel() {
            cancelled.set(true);
        }

        public boolean isCancelled() {
            return cancelled.get();
        }
    }
}
