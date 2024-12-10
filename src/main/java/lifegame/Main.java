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
        MainViewModel viewModel = new MainViewModel(new GameModel());
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> viewModel.saveState());
        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(e -> viewModel.loadState());
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        frame.setContentPane(new MainView(viewModel));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                viewModel.onDispose();
            }
        });
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setVisible(true);
        frame.pack();
    }
}
