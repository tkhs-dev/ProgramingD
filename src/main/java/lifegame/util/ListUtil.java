package lifegame.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    public static <T> T get2D(List<List<T>> list, int i, int j) {
        return list.get(i).get(j);
    }

    public static <T> void set2D(List<List<T>> list, int i, int j, T value) {
        list.get(i).set(j, value);
    }

    public static <T> List<List<T>> create2DArrayList(int row, int column, T defaultValue) {
        List<List<T>> list = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            List<T> r = new ArrayList<>();
            for (int j = 0; j < column; j++) {
                r.add(defaultValue);
            }
            list.add(r);
        }
        return list;
    }
}
