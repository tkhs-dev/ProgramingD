package lifegame.util;

public class BitBoardUtil {
    public static boolean isOn(long bitBoard, int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) {
            throw new IllegalArgumentException("Out of range");
        }
        return bitBoard << (x + y * 8) < 0;
    }

    public static long setOn(long bitBoard, int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) {
            throw new IllegalArgumentException("Out of range");
        }
        return bitBoard | (1L << 63 - (x + y * 8));
    }

    public static long setOff(long bitBoard, int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) {
            throw new IllegalArgumentException("Out of range");
        }
        return bitBoard & ~(1L << 63 - (x + y * 8));
    }

    public static void print(long bitBoard) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(isOn(bitBoard, j, i) ? "O  " : "-  ");
            }
            System.out.println();
        }
    }
}
