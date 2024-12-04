package lifegame;

import lifegame.model.GameModel;
import lifegame.view.main.MainView;
import lifegame.view.main.MainViewModel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::NewWindow);
    }

    public static void NewWindow() {
        JFrame frame = new JFrame("Lifegame");
        frame.setContentPane(new MainView(new MainViewModel(new GameModel())));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setVisible(true);
        frame.pack();
    }
}
