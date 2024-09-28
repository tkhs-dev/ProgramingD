package lifegame;

import lifegame.model.BoardModel;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        BoardModel model = new BoardModel(9, 9);
        model.setState(2, 2, true);
        model.setState(3, 3, true);
        model.setState(4, 4, true);
        model.setState(5, 5, true);
        model.setState(6, 6, true);
        model.setState(2, 6, true);
        model.setState(3, 5, true);
        model.setState(5, 3, true);
        model.setState(6, 2, true);
        System.out.println(model);
//        model.step();
        int i = 0;
        while (Arrays.stream(model.getBoard()).flatMapToLong(Arrays::stream).anyMatch(l -> l != 0)) {
            model.step();
            System.out.println(model);
            System.out.println("=====================================");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if (i > 100) {
                break;
            }
        }
//        JFrame frame = new JFrame("Life Game");
//        frame.setContentPane(new BoardPanel(10, 10));
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setPreferredSize(new Dimension(500, 500));
//        frame.setMinimumSize(new Dimension(500, 500));
//        frame.setVisible(true);
//        frame.pack();
    }
}
