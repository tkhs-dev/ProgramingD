package lifegame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Event<T> {
    private Observable<T> observable;

    public Event() {
        observable = new Observable<>();
    }

    public Observable<T> getObservable() {
        return observable;
    }

    public void notify(T value) {
        observable.notify(value);
    }

    public static class Observable<T> {
        private List<Consumer<T>> consumers;
        private Observable() {
            consumers = new ArrayList<>();
        }

        public void subscribe(Consumer<T> consumer) {
            consumers.add(consumer);
        }

        private void notify(T value) {
            consumers.forEach(consumer -> consumer.accept(value));
        }
    }
}
