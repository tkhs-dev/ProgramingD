package lifegame.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    /**
     * Get the element at the specified index of the 2D list.
     * @param list the 2D list
     * @param i the row index
     * @param j the column index
     * @param <T> the type of the element
     * @return the element at the specified index
     * @APINote This method is equivalent to
     * <pre>{@code
     * list.get(i).get(j);
     *}</pre>
     */
    public static <T> T get2D(List<List<T>> list, int i, int j) {
        return list.get(i).get(j);
    }

    /**
     * Set the element at the specified index of the 2D list.
     * @param list the 2D list
     * @param i the row index
     * @param j the column index
     * @param value the value to set
     * @param <T> the type of the element
     * @APINote This method is equivalent to
     * <pre>{@code
     * list.get(i).set(j, value);
     *}</pre>.
     */
    public static <T> void set2D(List<List<T>> list, int i, int j, T value) {
        list.get(i).set(j, value);
    }

    /**
     * Create a 2D list with the specified row and column size.
     * @param row the row size
     * @param column the column size
     * @param defaultValue the default value of the list
     * @param <T> the type of the element
     * @return the created 2D list
     * @APINote This method is equivalent to
     * <pre>{@code
     * List<List<T>> list = new ArrayList<>();
     * for (int i = 0; i < row; i++) {
     *     List<T> r = new ArrayList<>();
     *     for (int j = 0; j < column; j++) {
     *         r.add(defaultValue);
     *     }
     *     list.add(r);
     * }
     * return list;
     *}</pre>
     */
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
