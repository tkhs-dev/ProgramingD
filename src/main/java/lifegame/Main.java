package lifegame;

import lifegame.model.BoardModel;
import lifegame.view.main.MainView;
import lifegame.view.main.MainViewModel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        BoardModel model = new BoardModel(1, 1);
//        model.setState(0, 0, true);
//        model.setState(1, 0, true);
//        model.setState(2, 0, true);
//        model.setState(0, 1, true);
//        model.setState(1, 2, true);
//        model.setState(4, 0, true);
//        model.setState(4, 1, true);
//        model.setState(4, 2, true);
//        model.setState(0, 4, true);
//        model.setState(1, 4, true);
//        model.setState(2, 4, true);
//        model.setState(6, 4, true);
//        model.setState(7, 4, true);
//        model.setState(8, 4, true);
//        model.setState(4, 6, true);
//        model.setState(4, 7, true);
//        model.setState(4, 8, true);
//
//        System.out.println(model);
//        model.step();
//        int i = 0;
//        do {
//            model.step();
//            System.out.println(model);
//            System.out.println("=====================================");
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            i++;
//        } while (i <= 10);
        JFrame frame = new JFrame("Life Game");
        frame.setContentPane(new MainView(new MainViewModel(new BoardModel(1, 1))));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setVisible(true);
        frame.pack();
    }
}
