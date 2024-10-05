package lifegame.util;

import java.awt.*;
import java.util.function.Consumer;

public class Binding {
    public static <C extends Container,E> void bindBidirectionally(C container, String property, State<E> state) {
        state.onValueChange(value -> {
            try {
                ((Class<C>)container.getClass()).getField(property).set(container, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        container.addPropertyChangeListener(property, e -> {
            try {
                state.setValue((E) e.getNewValue());
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static <T> void bindSetter(State<T> state, Consumer<T> setter) {
        state.onValueChange(setter::accept);
        setter.accept(state.getValue());
    }
}
