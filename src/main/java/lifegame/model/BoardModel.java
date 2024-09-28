package lifegame.model;

import lifegame.util.BitBoardUtil;

import static java.lang.Math.ceil;

public class BoardModel {
    private final int row;
    private final int column;
    private final int rowChunk;
    private final int columnChunk;

    private long[][] board;

    public BoardModel(int row, int column) {
        this.row = row;
        this.column = column;
        this.rowChunk = (int) ceil(row / 8d);
        this.columnChunk = (int) ceil(column / 8d);
        this.board = new long[this.row + 2][this.column + 2];
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getRowChunk() {
        return rowChunk;
    }

    public int getColumnChunk() {
        return columnChunk;
    }

    public long[][] getBoard() {
        return board;
    }

    public void setState(int x, int y, boolean state) {
        if(x < 0 || x >= row || y < 0 || y >= column) {
            throw new IllegalArgumentException("Out of range");
        }
        int i = x / 8 + 1;
        int j = y / 8 + 1;
        int k = x % 8;
        int l = y % 8;
        if (state) {
            board[i][j] = BitBoardUtil.setOn(board[i][j], k, l);
        } else {
            board[i][j] = BitBoardUtil.setOff(board[i][j], k, l);
        }
    }

    public void step() {
        clip();
        long[][] nextBoard = new long[rowChunk + 2][columnChunk + 2];
        for (int i = 1; i <= rowChunk; i++) {
            for (int j = 1; j <= columnChunk; j++) {
                long UL = board[i - 1][j - 1];
                long U = board[i][j - 1];
                long UR = board[i + 1][j - 1];
                long L = board[i - 1][j];
                long R = board[i + 1][j];
                long DL = board[i - 1][j + 1];
                long D = board[i][j + 1];
                long DR = board[i + 1][j + 1];
                nextBoard[i][j] = nextGenChunk(board[i][j], UL, U, UR, L, R, DL, D, DR);
            }
        }
        board = nextBoard;
    }

    private void clip() {
        long rMask = 0xFEFEFEFEFEFEFEFEL;
        long bMask = 0xFFFFFFFFFFFFFF00L;

        for (int j = 0; j < (8 - row % 8) % 8; j++) {
            for (int i = 1; i <= columnChunk; i++) {
                board[rowChunk][i] &= rMask;
            }
            rMask <<= 1;
        }
        for (int i = 0; i < (8 - column % 8) % 8; i++) {
            for (int j = 1; j <= rowChunk; j++) {
                board[j][columnChunk] &= bMask;
            }
            bMask <<= 8;
        }
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
        int xCount;
        int yCount;

        StringBuilder sb = new StringBuilder();

        yCount = column;
        for (int i = 1; i < columnChunk + 1; i++) {
            for (int j = 0; j < Math.min(yCount, 8); j++) {
                xCount = row;
                for (int k = 1; k < rowChunk + 1; k++) {
                    for (int l = 0; l < Math.min(xCount, 8); l++) {
                        sb.append(BitBoardUtil.isOn(board[k][i], l, j) ? "O  " : "-  ");
                    }
                    xCount -= 8;
                }
                sb.append("\n");
            }
            yCount -= 8;
        }
        return sb.toString();
    }
}
