package lifegame.component;

import lifegame.util.Point;

import java.util.List;

/**
 * Represents the data of the board view.
 * @param board the board data
 * @param startChunkCoord the start coordinate of the board
 */
public record BoardViewData(List<List<Long>> board, Point startChunkCoord) {
    public BoardViewData {
        if (board == null) {
            throw new IllegalArgumentException("board must not be null");
        }
        if (startChunkCoord == null) {
            throw new IllegalArgumentException("startCoord must not be null");
        }
    }

    public static BoardViewData createEmpty() {
        return new BoardViewData(List.of(List.of(0L)), new Point(0, 0));
    }
}
