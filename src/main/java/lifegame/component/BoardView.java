package lifegame.component;

import lifegame.util.ListUtil;
import lifegame.util.Point;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class BoardView extends Canvas {
    private int row;
    private int column;
    private final int separatorWidth;
    private int cellSize;

    private BoardViewData board;
    private boolean[][] buffer;

    private Point screenStartCoord;

    public BoardView(int cellSize, int separatorWidth) {
        super();
        this.cellSize = cellSize;
        this.separatorWidth = separatorWidth;
        this.screenStartCoord = new Point(0, 0);
        this.board = new BoardViewData(ListUtil.create2DArrayList(1, 1, 0L), new Point(0, 0));
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateBoardSize();
                repaint();
            }
        });
    }

    public BoardView(int cellSize) {
        this(cellSize, 1);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        updateBoardSize();
        loadToBuffer();
        repaint();
    }

    public void updateBoard(BoardViewData board) {
        this.board = board;
        loadToBuffer();
        repaint();
    }

    private void updateBoardSize() {
        row = (int) floor((double) (getHeight() - separatorWidth) / (cellSize + separatorWidth));
        column = (int) floor((double) (getWidth() - separatorWidth) / (cellSize + separatorWidth));
        buffer = new boolean[row+2][column+2];
    }

    private void loadToBuffer() {
        Point chunkStart = new Point((int)floor(screenStartCoord.x/8d), (int)floor(screenStartCoord.y/8d));
        Point chunkEnd = chunkStart.add(new Point((int)ceil(column/8d), (int)ceil(row/8d)));
        Point offset = board.startChunkCoord();
        for (int i = chunkStart.y; i <= chunkEnd.y; i++) {
            for (int j = chunkStart.x; j <= chunkEnd.x; j++) {
                int chunkXStart = j * 8; //the x-coordinate of the chunk
                int chunkYStart = i * 8; //the y-coordinate of the chunk
                if (i - offset.y < 0 || j - offset.x < 0 || i - offset.y >= board.board().size() || j - offset.x >= board.board().get(0).size()) { //if the chunk is out of the board
//                    for (int k = 0; k < 8; k++) {
//                        for (int l = 0; l < 8; l++) {
//                            if(chunkYStart + k >= screenStartCoord.y && chunkYStart + k < screenStartCoord.y + row &&
//                                    chunkXStart + l >= screenStartCoord.x && chunkXStart + l < screenStartCoord.x + column) {
//                                buffer[chunkYStart + k - screenStartCoord.y + 1][chunkXStart + l - screenStartCoord.x + 1] = false; //set the cell to dead if it is in the screen and the chunk is out of the board
//                            }
//                        }
//                    }
                    continue;
                }
                long chunk = ListUtil.get2D(board.board(), i - offset.y, j - offset.x);
                for (int k = 0; k < 8; k++) { //k represents the y-coordinate of the cell
                    if(chunkYStart + k < screenStartCoord.y) {
                        chunk <<= 8;
                        continue;
                    }
                    if(chunkYStart + k > screenStartCoord.y + row) {
                        break;
                    }
                    for (int l = 0; l < 8; l++) { //l represents the x-coordinate of the cell
                        if(chunkXStart + l < screenStartCoord.x) {
                            chunk <<= 1;
                            continue;
                        }
                        if(chunkXStart + l > screenStartCoord.x + column) {
                            chunk <<= 8 - l;
                            break;
                        }
                        buffer[chunkYStart + k - screenStartCoord.y][chunkXStart + l - screenStartCoord.x] = chunk < 0;
                        chunk <<= 1;
                    }
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

//        for (int i = 0; i < row + 1; i++) {
//            g.setColor(Color.GRAY);
//            g.drawLine(separatorWidth, separatorWidth + i * (cellSize + separatorWidth), separatorWidth + column * (cellSize + separatorWidth), separatorWidth + i * (cellSize + separatorWidth));
//        }
//        for (int i = 0; i < column + 1; i++) {
//            g.setColor(Color.GRAY);
//            g.drawLine(separatorWidth + i * (cellSize + separatorWidth), separatorWidth, separatorWidth + i * (cellSize + separatorWidth), separatorWidth + row * (cellSize + separatorWidth));
//        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (buffer[i + 1][j + 1]) {
                    g.setColor(Color.YELLOW);
                }else{
                    g.setColor(Color.WHITE);
                }
                g.fillRect(separatorWidth + j * (cellSize + separatorWidth) + 1, separatorWidth + i * (cellSize + separatorWidth) + 1, cellSize - 1, cellSize - 1);
            }
        }
    }
}
