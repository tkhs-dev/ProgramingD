package lifegame.model;

import lifegame.util.BitBoardUtil;
import lifegame.util.Direction;
import lifegame.util.ListUtil;
import lifegame.util.Point;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class BoardState implements Cloneable, Externalizable {
    private int columnChunk;
    private int rowChunk;

    private List<List<Long>> board;
    private Point startCoord;

    public BoardState() {
        this(1, 1, ListUtil.create2DArrayList(3, 3, 0L), new Point(-1, -1));
    }

    private BoardState(int columnChunk, int rowChunk, List<List<Long>> board, Point startCoord) {
        this.columnChunk = columnChunk;
        this.rowChunk = rowChunk;
        this.board = board;
        this.startCoord = startCoord;
    }

    public Point getStartCoord() {
        return startCoord;
    }

    public List<List<Long>> getBoard() {
        return board;
    }

    public boolean getCellState(int x, int y) {
        int chunkX = (int)floor((x - startCoord.x * 8) / 8d);
        int chunkY = (int)floor((y - startCoord.y * 8) / 8d);
        if (chunkX < 0 || chunkX >= columnChunk || chunkY < 0 || chunkY >= rowChunk) {
            return false;
        }
        int k = (x - startCoord.x * 8) % 8;
        int l = (y - startCoord.y * 8) % 8;
        return BitBoardUtil.isOn(ListUtil.get2D(board, chunkY, chunkX), k, l);
    }

    public void changeCellState(int x, int y, boolean state) {
        if (x < startCoord.x * 8) {
            expandBoard(Direction.LEFT, (int) ceil((startCoord.x * 8 - x) / 8d));
        } else if (x >= (startCoord.x + columnChunk) * 8) {
            expandBoard(Direction.RIGHT, (int) ceil((x - (startCoord.x + columnChunk) * 8) / 8d));
        }
        if (y < startCoord.y * 8) {
            expandBoard(Direction.UP, (int) ceil((startCoord.y * 8 - y) / 8d));
        } else if (y >= (startCoord.y + rowChunk) * 8) {
            expandBoard(Direction.DOWN, (int) ceil((y - (startCoord.y + rowChunk) * 8) / 8d));
        }

        int chunkX = (int)floor((x - startCoord.x * 8) / 8d);
        int chunkY = (int)floor((y - startCoord.y * 8) / 8d);
        int k = (x - startCoord.x * 8) % 8;
        int l = (y - startCoord.y * 8) % 8;
        if (state) {
            ListUtil.set2D(board, chunkY, chunkX, BitBoardUtil.setOn(ListUtil.get2D(board, chunkY, chunkX), k, l));
        } else {
            ListUtil.set2D(board, chunkY, chunkX, BitBoardUtil.setOff(ListUtil.get2D(board, chunkY, chunkX), k, l));
        }
    }

    private void trim(){
        int newColumnChunk = columnChunk;
        int newRowChunk = rowChunk;
        for (int i = 0; i < rowChunk + 2 && newRowChunk >= 2; i++) {
            if(board.get(1).stream().allMatch(l -> l == 0)){
                board.remove(0);
                newRowChunk--;
            }else {
                break;
            }
        }
        for (int i = 0; i < columnChunk + 2 && newColumnChunk >= 2; i++) {
            if(board.stream().allMatch(l -> l.get(1) == 0)){
                for (List<Long> l : board) {
                    l.remove(0);
                }
                newColumnChunk--;
            }else {
                break;
            }
        }
        rowChunk = newRowChunk;
        columnChunk = newColumnChunk;
        for (int i = rowChunk + 1; i >= 0 && rowChunk >= 2; i--) {
            if(board.get(rowChunk).stream().allMatch(l -> l == 0)){
                board.remove(rowChunk);
                rowChunk--;
            }else {
                break;
            }
        }
        for (int i = columnChunk + 1; i >= 0 && columnChunk >= 2; i--) {
            if(board.stream().allMatch(l -> l.get(columnChunk) == 0)){
                for (List<Long> l : board) {
                    l.remove(columnChunk);
                }
                columnChunk--;
            }else {
                break;
            }
        }
        startCoord = new Point(-1,-1);
    }

    private synchronized void expandBoard(Direction direction, int n) {
        switch (direction) {
            case UP:
                for (int i = 0; i < n; i++) {
                    List<Long> r = new ArrayList<>();
                    for (int j = 0; j < columnChunk + 2; j++) {
                        r.add(0L);
                    }
                    board.add(0, r);
                }
                rowChunk += n;
                startCoord.y -= n;
                break;
            case DOWN:
                for (int i = 0; i < n; i++) {
                    List<Long> r = new ArrayList<>();
                    for (int j = 0; j < columnChunk + 2; j++) {
                        r.add(0L);
                    }
                    board.add(r);
                }
                rowChunk += n;
                break;
            case LEFT:
                for (int i = 0; i < n; i++) {
                    for (List<Long> l : board) {
                        l.add(0, 0L);
                    }
                }
                columnChunk += n;
                startCoord.x -= n;
                break;
            case RIGHT:
                for (int i = 0; i < n; i++) {
                    for (List<Long> l : board) {
                        l.add(0L);
                    }
                }
                columnChunk += n;
                break;
        }

    }

    public void nextState() {
        //expand board if necessary
        if(board.get(1).stream().anyMatch(l -> l!=0)) { //if the first row has a cell
            expandBoard(Direction.UP, 1);
        }
        if(board.get(rowChunk).stream().anyMatch(l -> l!=0)) { //if the last row has a cell
            expandBoard(Direction.DOWN, 1);
        }
        if(board.stream().anyMatch(l -> l.get(1) != 0)) { //if the first column has a cell
            expandBoard(Direction.LEFT, 1);
        }
        if(board.stream().anyMatch(l -> l.get(columnChunk) != 0)) { //if the last column has a cell
            expandBoard(Direction.RIGHT, 1);
        }

        List<List<Long>> nextBoard = ListUtil.create2DArrayList(rowChunk + 2, columnChunk + 2, 0L);
        for (int i = 1; i <= rowChunk; i++) {
            for (int j = 1; j <= columnChunk; j++) {
                long UL = ListUtil.get2D(board, i - 1, j - 1);
                long U = ListUtil.get2D(board, i - 1, j);
                long UR = ListUtil.get2D(board, i - 1, j + 1);
                long L = ListUtil.get2D(board, i, j - 1);
                long R = ListUtil.get2D(board, i, j + 1);
                long DL = ListUtil.get2D(board, i + 1, j - 1);
                long D = ListUtil.get2D(board, i + 1, j);
                long DR = ListUtil.get2D(board, i + 1, j + 1);
                long chunk = ListUtil.get2D(board, i, j);

                ListUtil.set2D(nextBoard, i, j, nextGenChunk(chunk, UL, U, UR, L, R, DL, D, DR));
            }
        }
        board = nextBoard;
    }

    /**
     * @see <a href="http://vivi.dyndns.org/tech/games/LifeGame.html">http://vivi.dyndns.org/tech/games/LifeGame.html</a>
     */
    private static long nextGenChunk(long chunk, long UL, long U, long UR, long L, long R, long DL, long D, long DR) {
        long a, b, c, d, e, f, g, h;
        a = ((UL & 0x0000000000000001L) << 63) | ((U & 0x00000000000000FEL) << 55) |
                ((L & 0x0101010101010100L) >>> 1) | ((chunk & 0xFEFEFEFEFEFEFEFEL) >>> 9);

        b = ((U & 0x00000000000000FFL) << 56) | ((chunk & 0xFFFFFFFFFFFFFF00L) >>> 8);

        c = ((U & 0x000000000000007FL) << 57) | ((UR & 0x0000000000000080L) << 49) |
                ((chunk & 0x7F7F7F7F7F7F7F7FL) >>> 7) | ((R & 0x8080808080808000L) >>> 15);

        d = ((L & 0x0101010101010101L) << 7) | ((chunk & 0xFEFEFEFEFEFEFEFEL) >>> 1);

        e = ((chunk & 0x7F7F7F7F7F7F7F7FL) << 1) | ((R & 0x8080808080808080L) >>> 7);

        f = ((L & 0x0001010101010101L) << 15) | ((chunk & 0x00FEFEFEFEFEFEFEL) << 7) |
                ((DL & 0x0100000000000000L) >>> 49) | ((D & 0xFE00000000000000L) >>> 57);

        g = ((chunk & 0x00FFFFFFFFFFFFFFL) << 8) | ((D & 0xFF00000000000000L) >>> 56);

        h = ((chunk & 0x007F7F7F7F7F7F7FL) << 9) | ((R & 0x0080808080808080L) << 1) |
                ((D & 0x7F00000000000000L) >>> 55) | ((DR & 0x8000000000000000L) >>> 63);

        long xab, xcd, xef, xgh, x, s2, s3;
        //copied from http://vivi.dyndns.org/tech/games/LifeGame.html
        xab = a & b;
        a ^= b;
        xcd = c & d;
        c ^= d;
        xef = e & f;
        e ^= f;
        xgh = g & h;
        g ^= h;
        d = a & c;
        a ^= c;
        c = xab & xcd;
        b = xab ^ xcd ^ d;
        h = e & g;
        e ^= g;
        g = xef & xgh;
        f = xef ^ xgh ^ h;
        d = a & e;
        a ^= e;
        h = b & f;
        b ^= f;
        h |= b & d;
        b ^= d;
        c ^= g ^ h;
        x = ~c & b;
        s2 = x & ~a;
        s3 = x & a;
        return s3 | (chunk & s2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < rowChunk + 1; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 1; k < columnChunk + 1; k++) {
                    for (int l = 0; l < 8; l++) {
                        sb.append(BitBoardUtil.isOn(ListUtil.get2D(board,i,k), l, j) ? "O  " : "-  ");
                    }
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public BoardState clone() {
        BoardState board=null;

        try {
            board=(BoardState)super.clone();
            List<List<Long>> tmp = new ArrayList<>();
            for (List l : this.board) {
                List<Long> r = new ArrayList<>();
                for (Object o : l) {
                    r.add((Long) o);
                }
                tmp.add(r);
            }
            board.board = tmp;
            board.rowChunk = this.rowChunk;
            board.columnChunk = this.columnChunk;
            board.startCoord = this.startCoord.clone();
        }catch (Exception e){
            e.printStackTrace();
        }
        return board;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        BoardState clone = this.clone();
        clone.trim();
        out.writeInt(clone.columnChunk);
        out.writeInt(clone.rowChunk);
        for (List<Long> l : clone.board) {
            for (Long aLong : l) {
                out.writeLong(aLong);
            }
        }
        out.writeInt(clone.startCoord.x);
        out.writeInt(clone.startCoord.y);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        columnChunk = in.readInt();
        rowChunk = in.readInt();
        board = new ArrayList<>();
        for (int i = 0; i < rowChunk + 2; i++) {
            List<Long> l = new ArrayList<>();
            for (int j = 0; j < columnChunk + 2; j++) {
                l.add(in.readLong());
            }
            board.add(l);
        }
        startCoord = new Point(in.readInt(), in.readInt());
    }
}
