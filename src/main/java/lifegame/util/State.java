package lifegame.util;

import java.util.ArrayList;
import java.util.List;

public class State<T> {
    private T value;
    private List<Listener<T>> listeners;

    public State(T initialValue) {
        listeners = new ArrayList<>();
        this.value = initialValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        listeners.forEach(listener -> listener.onChange(value));
    }

    public void onValueChange(Listener<T> listener) {
        listeners.add(listener);
    }

    public interface Listener<T> {
        void onChange(T state);
    }
}
