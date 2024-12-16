package lifegame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Observable<T> {
    void subscribe(Observer<? super T> observer);

    default void subscribe(Consumer<T> onNext) {
        this.subscribe(onNext, throwable -> {}, () -> {});
    }

    default void subscribe(Consumer<T> onNext, Consumer<Throwable> onError, Runnable onComplete) {
        this.subscribe(new Observer<>() {
            @Override
            public void onNext(T value) {
                onNext.accept(value);
            }

            @Override
            public void onError(Throwable throwable) {
                onError.accept(throwable);
            }

            @Override
            public void onComplete() {
                onComplete.run();
            }
        });
    }

    static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        return observer -> {
            ObservableEmitter<T> emitter = new ObservableEmitter<>() {
                @Override
                public void onNext(T value) {
                    observer.onNext(value);
                }

                @Override
                public void onError(Throwable throwable) {
                    observer.onError(throwable);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            };

            try {
                source.subscribe(emitter);
            } catch (Throwable t) {
                emitter.onError(t);
            }
        };
    }

    // just: 単一の値を持つ Observable を生成
    static <T> Observable<T> just(T item) {
        return observer -> {
            try {
                observer.onNext(item);
            } catch (Throwable t) {
                observer.onError(t);
            }
        };
    }

    // map: 各要素に関数を適用する
    default <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return observer -> subscribe(item ->{
            try {
                observer.onNext(mapper.apply(item));
            } catch (Throwable t) {
                observer.onError(t);
            }
        }, observer::onError, observer::onComplete);
    }

    // filter: 条件を満たす要素のみを通過させる
    default Observable<T> filter(Predicate<? super T> predicate) {
        return observer -> subscribe(item -> {
            try {
                if (predicate.test(item)) {
                    observer.onNext(item);
                }
            } catch (Throwable t) {
                observer.onError(t);
            }
        }, observer::onError, observer::onComplete);
    }

    // distinctUntilChanged: 連続した重複を除外
    default Observable<T> distinctUntilChanged() {
        return observer -> subscribe(new Observer<>() {
            T last = null;
            boolean first = true;

            @Override
            public void onNext(T item) {
                if (first || (last != null && !last.equals(item))) {
                    first = false;
                    last = item;
                    observer.onNext(item);
                }
            }

            @Override
            public void onComplete() {
                observer.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                observer.onError(throwable);
            }
        });
    }

    default Observable<T> merge(Observable<? extends T> other) {
        return observer -> {
            subscribe(item -> {
                try {
                    observer.onNext(item);
                } catch (Throwable t) {
                    observer.onError(t);
                }
            }, observer::onError, observer::onComplete);
            other.subscribe(item -> {
                try {
                    observer.onNext(item);
                } catch (Throwable t) {
                    observer.onError(t);
                }
            }, observer::onError, observer::onComplete);
        };
    }

    // switchMap: 新しい Observable に切り替え
    default <R> Observable<R> switchMap(Function<? super T, Observable<? extends R>> mapper) {
        return observer -> {
            subscribe(item->{
                Observable<? extends R> newObservable = mapper.apply(item);
                newObservable.subscribe(new Observer<R>() {
                    @Override
                    public void onNext(R value) {
                        observer.onNext(value);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        observer.onError(throwable);
                    }
                });
            }, observer::onError, observer::onComplete);
        };
    }

    default Observable<T> publish() {
        List<Observer<? super T>> observers = new ArrayList<>();
        boolean[] isEmitting = {false}; // 状態を保持するためのフラグ

        return observer -> {
            synchronized (observers) {
                observers.add(observer); // 新しいObserverをリストに追加
            }

            if (!isEmitting[0]) { // データの発行がまだ始まっていない場合
                isEmitting[0] = true;
                Observable.this.subscribe(new Observer<>() {
                    @Override
                    public void onNext(T item) {
                        synchronized (observers) {
                            for (Observer<? super T> obs : observers) {
                                obs.onNext(item);
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        synchronized (observers) {
                            for (Observer<? super T> obs : observers) {
                                obs.onComplete();
                            }
                            observers.clear(); // 完了後はObserverを全て削除
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        synchronized (observers) {
                            for (Observer<? super T> obs : observers) {
                                obs.onError(throwable);
                            }
                            observers.clear(); // エラー後はObserverを全て削除
                        }
                    }
                });
            }
        };
    }

    interface Observer<T> {
        void onNext(T item);
        void onComplete();
        void onError(Throwable throwable);
    }

    interface ObservableEmitter<T> {
        void onNext(T value);
        void onError(Throwable throwable);
        void onComplete();
    }

    @FunctionalInterface
    interface ObservableOnSubscribe<T> {
        void subscribe(ObservableEmitter<T> emitter) throws Exception;
    }
}