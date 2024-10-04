package lifegame.view.main;

import lifegame.component.BoardView;
import lifegame.component.BoardViewData;
import lifegame.model.BoardModel;
import lifegame.util.Binding;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

public class MainView extends JPanel {
    private BoardView boardView;
    private BoardModel model;

    private MainViewModel viewModel;

    public MainView(MainViewModel viewModel) {
        super();

        this.viewModel = viewModel;
        model = new BoardModel(1, 1);
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
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                model.setState(i, j, random.nextBoolean());
            }
        }

        initializeUi();
    }

    private void initializeUi() {
        JPanel gamePanel = new JPanel();
        SpringLayout springLayout = new SpringLayout();
        gamePanel.setLayout(springLayout);

        JPanel viewSettingPanel = new JPanel();
        viewSettingPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
        viewSettingPanel.setBackground(Color.LIGHT_GRAY);
        viewSettingPanel.setLayout(new GridLayout(2, 1));
        JSlider scaleSlider = new JSlider(1, 100, 10);
        scaleSlider.addChangeListener(e -> viewModel.onScaleChange(scaleSlider.getValue()));
        viewSettingPanel.add(new JLabel("View Setting"));
        viewSettingPanel.add(scaleSlider);
        viewSettingPanel.setPreferredSize(new Dimension(180, 100));
        springLayout.putConstraint(SpringLayout.SOUTH, viewSettingPanel, -30, SpringLayout.SOUTH, gamePanel);
        springLayout.putConstraint(SpringLayout.EAST, viewSettingPanel, -30, SpringLayout.EAST, gamePanel);
        gamePanel.add(viewSettingPanel);

        boardView = new BoardView(15);
        Binding.bindSetter(viewModel.scale, boardView::setCellSize);
        Binding.bindSetter(viewModel.board, boardView::updateBoard);
        springLayout.putConstraint(SpringLayout.NORTH, boardView, 10, SpringLayout.NORTH, gamePanel);
        springLayout.putConstraint(SpringLayout.WEST, boardView, 10, SpringLayout.WEST, gamePanel);
        springLayout.putConstraint(SpringLayout.SOUTH, boardView, 0, SpringLayout.SOUTH, gamePanel);
        springLayout.putConstraint(SpringLayout.EAST, boardView, 0, SpringLayout.EAST, gamePanel);
        gamePanel.add(boardView);

        gamePanel.setComponentZOrder(viewSettingPanel, 0);
        gamePanel.setComponentZOrder(boardView, 1);
        gamePanel.revalidate();
        gamePanel.repaint();

        JPanel controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(500, 50));
        controlPanel.setLayout(new GridLayout(1, 5, 20, 0));
        controlPanel.setBorder(new EmptyBorder(0, 25, 10, 25));
        JButton startButton = new JButton("Start");
        JButton stepButton = new JButton("Next");
        JButton undoButton = new JButton("Undo");
        JButton resetButton = new JButton("Reset");
        JButton newGameButton = new JButton("NewGame");
        Binding.bindSetter(viewModel.isRunning, b ->{startButton.setText(b ? "Stop" : "Start");});
        Binding.bindSetter(viewModel.undoEnabled, undoButton::setEnabled);
        startButton.addActionListener(e -> viewModel.onStartClick());
        stepButton.addActionListener(e -> viewModel.onStepClick());
        undoButton.addActionListener(e -> viewModel.onUndoClick());
        resetButton.addActionListener(e -> viewModel.onResetClick());
        newGameButton.addActionListener(e -> viewModel.onNewGameClick());
        controlPanel.add(startButton);
        controlPanel.add(stepButton);
        controlPanel.add(undoButton);
        controlPanel.add(resetButton);
        controlPanel.add(newGameButton);

        GridBagLayout gridBagLayout = new GridBagLayout();
        this.setLayout(gridBagLayout);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_START;
        gridBagLayout.setConstraints(gamePanel, c);
        this.add(gamePanel);

        c.gridy = 1;
        c.weighty = 0.0;
        gridBagLayout.setConstraints(controlPanel, c);
        this.add(controlPanel);
    }
}
