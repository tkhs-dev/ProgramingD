package lifegame.model;

import lifegame.util.BitBoardUtil;
import lifegame.util.ListUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardModel {
    private int columnChunk;
    private int rowChunk;

    private List<List<Long>> board;
    private Point startCoord;

    public BoardModel(int columnChunk, int rowChunk) {
        this.columnChunk = columnChunk;
        this.rowChunk = rowChunk;
        this.board = new ArrayList<>();
        this.startCoord = new Point(0, 0);

        for (int i = 0; i < this.columnChunk + 2; i++) {
            List<Long> r = new ArrayList<>();
            for (int j = 0; j < this.rowChunk + 2; j++) {
                r.add(0L);
            }
            board.add(r);
        }
    }

    public int getColumnChunk() {
        return columnChunk;
    }

    public int getRowChunk() {
        return rowChunk;
    }

    public List<List<Long>> getBoard() {
        return board;
    }

    public void setState(int x, int y, boolean state) {
        if (x < startCoord.x || y < startCoord.y || x > startCoord.x + columnChunk * 8 || y > startCoord.y + rowChunk * 8) {
            throw new IllegalArgumentException("x and y must be greater than 0");
        }

        int i = x / 8 + 1;
        int j = y / 8 + 1;
        int k = x % 8;
        int l = y % 8;
        if (state) {
            ListUtil.set2D(board, i, j, BitBoardUtil.setOn(ListUtil.get2D(board, i, j), k, l));
        } else {
            ListUtil.set2D(board, i, j, BitBoardUtil.setOff(ListUtil.get2D(board, i, j), k, l));
        }
    }

    public void step() {
        //expand board if necessary
        if(board.get(1).stream().anyMatch(l -> l!=0)) {
            List<Long> r = new ArrayList<>();
            for (int j = 0; j < rowChunk + 2; j++) {
                r.add(0L);
            }
            board.add(0, r);
            columnChunk++;
            startCoord.x--;
        }
        if(board.get(columnChunk).stream().anyMatch(l -> l!=0)) {
            List<Long> r = new ArrayList<>();
            for (int j = 0; j < rowChunk + 2; j++) {
                r.add(0L);
            }
            board.add(r);
            columnChunk++;
        }
        if(board.stream().anyMatch(l -> l.get(1) != 0)) {
            for (List<Long> l : board) {
                l.add(0, 0L);
            }
            rowChunk++;
            startCoord.y--;
        }
        if(board.stream().anyMatch(l -> l.get(rowChunk) != 0)) {
            for (List<Long> l : board) {
                l.add(0L);
            }
            rowChunk++;
        }

        List<List<Long>> nextBoard = new ArrayList<>();
        for (int i = 0; i < columnChunk + 2; i++) {
            List<Long> r = new ArrayList<>();
            for (int j = 0; j < rowChunk + 2; j++) {
                r.add(0L);
            }
            nextBoard.add(r);
        }
        for (int i = 1; i <= columnChunk; i++) {
            for (int j = 1; j <= rowChunk; j++) {
                long UL = ListUtil.get2D(board, i - 1, j - 1);
                long U = ListUtil.get2D(board, i, j - 1);
                long UR = ListUtil.get2D(board, i + 1, j - 1);
                long L = ListUtil.get2D(board, i - 1, j);
                long R = ListUtil.get2D(board, i + 1, j);
                long DL = ListUtil.get2D(board, i - 1, j + 1);
                long D = ListUtil.get2D(board, i, j + 1);
                long DR = ListUtil.get2D(board, i + 1, j + 1);
                long chunk = ListUtil.get2D(board, i, j);

                ListUtil.set2D(nextBoard, i, j, nextGenChunk(chunk, UL, U, UR, L, R, DL, D, DR));
            }
        }
        board = nextBoard;
    }

    /**
     * @param chunk target chunk
     * @param UL    upper left chunk
     * @param U     upper chunk
     * @param UR    upper right chunk
     * @param L     left chunk
     * @param R     right chunk
     * @param DL    down left chunk
     * @param D     down chunk
     * @param DR    down right chunk
     * @return
     * @see <a href="http://vivi.dyndns.org/tech/games/LifeGame.html">http://vivi.dyndns.org/tech/games/LifeGame.html</a>
     */
    private static long nextGenChunk(long chunk, long UL, long U, long UR, long L, long R, long DL, long D, long DR) {
        long a, b, c, d, e, f, g, h;
        a = ((UL & 0x0000000000000001L) << 63) | ((U & 0x00000000000000FEL) << 55) |
                ((L & 0x0101010101010100L) >>> 1) | ((chunk & 0xFEFEFEFEFEFEFEFEL) >>> 9);
//        System.out.println("------------a------------");
//        BitBoardUtil.print(a);

        b = ((U & 0x00000000000000FFL) << 56) | ((chunk & 0xFFFFFFFFFFFFFF00L) >>> 8);
//        System.out.println("------------b------------");
//        BitBoardUtil.print(b);

        c = ((U & 0x000000000000007FL) << 57) | ((UR & 0x0000000000000080L) << 49) |
                ((chunk & 0x7F7F7F7F7F7F7F7FL) >>> 7) | ((R & 0x8080808080808000L) >>> 15);
//        System.out.println("------------c------------");
//        BitBoardUtil.print(c);

        d = ((L & 0x0101010101010101L) << 7) | ((chunk & 0xFEFEFEFEFEFEFEFEL) >>> 1);
//        System.out.println("------------d------------");
//        BitBoardUtil.print(d);

        e = ((chunk & 0x7F7F7F7F7F7F7F7FL) << 1) | ((R & 0x8080808080808080L) >>> 7);
//        System.out.println("------------e------------");
//        BitBoardUtil.print(e);

        f = ((L & 0x0001010101010101L) << 15) | ((chunk & 0x00FEFEFEFEFEFEFEL) << 7) |
                ((DL & 0x0100000000000000L) >>> 49) | ((D & 0xFE00000000000000L) >>> 57);
//        System.out.println("------------f------------");
//        BitBoardUtil.print(f);

        g = ((chunk & 0x00FFFFFFFFFFFFFFL) << 8) | ((D & 0xFF00000000000000L) >>> 56);
//        System.out.println("------------g------------");
//        BitBoardUtil.print(g);

        h = ((chunk & 0x007F7F7F7F7F7F7FL) << 9) | ((R & 0x0080808080808080L) << 1) |
                ((D & 0x7F00000000000000L) >>> 55) | ((DR & 0x8000000000000000L) >>> 63);
//        System.out.println("------------h------------");
//        BitBoardUtil.print(h);

        long xab, xcd, xef, xgh, x, s2, s3;
        //↓ copied from http://vivi.dyndns.org/tech/games/LifeGame.html
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
                        sb.append(BitBoardUtil.isOn(ListUtil.get2D(board,k,i), l, j) ? "O  " : "-  ");
                    }
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
