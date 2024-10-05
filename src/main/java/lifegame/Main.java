package lifegame;

import lifegame.model.GameModel;
import lifegame.view.main.MainView;
import lifegame.view.main.MainViewModel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Life Game");
        frame.setContentPane(new MainView(new MainViewModel(new GameModel())));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setVisible(true);
        frame.pack();
    }
}
