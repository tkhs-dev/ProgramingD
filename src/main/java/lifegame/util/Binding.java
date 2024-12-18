package lifegame.util;

import java.util.function.Consumer;

public class Binding {
    public static <T> void bindSetter(State<T> state, Consumer<T> setter) {
        state.onValueChange(setter::accept);
        setter.accept(state.getValue());
    }
}
