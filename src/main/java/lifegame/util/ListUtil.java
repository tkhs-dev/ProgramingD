package lifegame.util;

import java.util.List;

public class ListUtil {
    public static <T> T get2D(List<List<T>> list, int i, int j) {
        return list.get(i).get(j);
    }

    public static <T> void set2D(List<List<T>> list, int i, int j, T value) {
        list.get(i).set(j, value);
    }
}
