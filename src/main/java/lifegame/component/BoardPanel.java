package lifegame.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.stream.IntStream;

public class BoardPanel extends JPanel {
    private final int row;
    private final int column;
    private int separaterWidth = 1;

    private int cellSize;

    public BoardPanel(int row, int column, int separaterWidth) {
        super();
        this.row = row;
        this.column = column;
        this.separaterWidth = separaterWidth;
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                cellSize = Math.min((getWidth() - separaterWidth * (column + 1) - 1) / column, (getHeight() - separaterWidth * (row + 1) - 1) / row);
                repaint();
            }
        });
    }

    public BoardPanel(int row, int column) {
        this(row, column, 1);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        IntStream.range(0, row + 1).forEach(i -> {
            g.setColor(Color.GRAY);
            g.drawLine(separaterWidth, separaterWidth + i * (cellSize + separaterWidth), separaterWidth + column * (cellSize + separaterWidth), separaterWidth + i * (cellSize + separaterWidth));
        });
        IntStream.range(0, column + 1).forEach(i -> {
            g.setColor(Color.GRAY);
            g.drawLine(separaterWidth + i * (cellSize + separaterWidth), separaterWidth, separaterWidth + i * (cellSize + separaterWidth), separaterWidth + row * (cellSize + separaterWidth));
        });
        IntStream.range(0, row).forEach(i -> {
            IntStream.range(0, column).forEach(j -> {
                g.setColor(Color.WHITE);
                g.fillRect(separaterWidth + j * (cellSize + separaterWidth) + 1, separaterWidth + i * (cellSize + separaterWidth) + 1, cellSize - 1, cellSize - 1);
            });
        });
    }
}
