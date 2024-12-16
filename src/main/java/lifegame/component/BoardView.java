package lifegame.component;

import lifegame.util.ListUtil;
import lifegame.util.Point;
import lifegame.util.Rx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class BoardView extends JPanel{
    private int row;
    private int column;
    private int separatorWidth;
    private int cellSize;

    private BoardViewData board;
    private boolean[][] buffer;

    private Point currentScrollPosition;

    Rx.Observable<MouseEvent> mousePressed;
    Rx.Observable<MouseEvent> mouseDragged;
    Rx.Observable<MouseEvent> mouseReleased;
    public Rx.Observable<Point> interactEvent;

    public BoardView(int cellSize, int separatorWidth) {
        super();

        mousePressed = Rx.Observable.create(emitter->{
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    e.consume();
                    e.setSource(BoardView.this);
                    emitter.next(e);
                }
            });
        });

        mouseDragged = Rx.Observable.<MouseEvent>create(emitter->{
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    e.consume();
                    e.setSource(BoardView.this);
                    emitter.next(e);
                }
            });
        });

        mouseReleased = Rx.Observable.create(emitter->{
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    e.consume();
                    e.setSource(BoardView.this);
                    emitter.next(e);
                }
            });
        });

        interactEvent = mousePressed
                .switchMap( event ->
                    Rx.Observable.just(event)
                        .merge(mouseDragged)
                        .filter(e -> SwingUtilities.isLeftMouseButton(event))
                        .map(e -> transformScreenCoordToBoardCoord(e.getX(), e.getY()))
                        .distinctUntilChanged()
                ).filter(e->e.x>= currentScrollPosition.x && e.x < currentScrollPosition.x + column && e.y >= currentScrollPosition.y && e.y < row + currentScrollPosition.y);



        mousePressed
                .switchMap(event -> {
                            var point = transformScreenCoordToBoardCoord(event.getX(), event.getY());
                            return mouseDragged
                                    .filter(e -> SwingUtilities.isMiddleMouseButton(event))
                                    .map(e -> new Point(e.getX(), e.getY()))
                                    .distinctUntilChanged()
                                    .map(e -> point.sub(transformScreenCoordToBoardCoord(e.x, e.y)));
                        }
                ).subscribe(p -> {
                    currentScrollPosition = currentScrollPosition.add(p);
                    loadToBuffer();
                    repaint();
                });

        this.cellSize = cellSize;
        this.separatorWidth = separatorWidth;
        this.currentScrollPosition = new Point(0, 0);
        this.board = new BoardViewData(ListUtil.create2DArrayList(1, 1, 0L), new Point(0, 0));
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateBoardSize();
                loadToBuffer();
            }
        });
    }

    public BoardView(int cellSize) {
        this(cellSize, 0);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getSeparatorWidth() {
        return separatorWidth;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        updateBoardSize();
        loadToBuffer();
        repaint();
    }

    public int getCellSize() {
        return cellSize;
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

    private Point transformScreenCoordToBoardCoord(int x, int y) {
        return new Point((int) floor((double) (x - separatorWidth) / (cellSize + separatorWidth)) + currentScrollPosition.x, (int) floor((double) (y - separatorWidth) / (cellSize + separatorWidth)) + currentScrollPosition.y);
    }

    private void loadToBuffer() {
        clearBuffer();
        Point chunkStart = new Point((int)floor((currentScrollPosition.x - 1)/8d), (int)floor((currentScrollPosition.y - 1)/8d));
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
                    if(chunkYStart + k < currentScrollPosition.y - 1) {
                        chunk <<= 8;
                        continue;
                    }
                    if(chunkYStart + k > currentScrollPosition.y + row) {
                        break;
                    }
                    for (int l = 0; l < 8; l++) { //l represents the x-coordinate of the cell
                        if(chunkXStart + l < currentScrollPosition.x - 1) {
                            chunk <<= 1;
                            continue;
                        }
                        if(chunkXStart + l > currentScrollPosition.x + column) {
                            chunk <<= 8 - l;
                            break;
                        }
                        buffer[chunkYStart + k - currentScrollPosition.y + 1][chunkXStart + l - currentScrollPosition.x + 1] = chunk < 0;
                        chunk <<= 1;
                    }
                }
            }
        }
    }

    private void clearBuffer(){
        for (int i = 0; i < row + 2; i++) {
            for (int j = 0; j < column + 2; j++) {
                buffer[i][j] = false;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
                    g.setColor(Color.RED);
                }else{
                    g.setColor(Color.WHITE);
                }
                g.fillRect(separatorWidth + j * (cellSize + separatorWidth) + 1, separatorWidth + i * (cellSize + separatorWidth) + 1, cellSize - 1, cellSize - 1);
            }
        }
    }
}
