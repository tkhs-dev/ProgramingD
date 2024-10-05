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
    private BoardModel model;

    private MainViewModel viewModel;

    public MainView(MainViewModel viewModel) {
        super();

        this.viewModel = viewModel;
        initializeUi();
    }

    private void initializeUi() {
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        BoardView boardView = new BoardView(15);
        SpringLayout springLayout = new SpringLayout();
        boardView.setLayout(springLayout);
        Binding.bindSetter(viewModel.scale, boardView::setCellSize);
        Binding.bindSetter(viewModel.board, boardView::updateBoard);

        JPanel viewSettingPanel = new JPanel();
        viewSettingPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
        viewSettingPanel.setBackground(Color.LIGHT_GRAY);
        viewSettingPanel.setLayout(new GridLayout(2, 1));
        JSlider scaleSlider = new JSlider(3, 100, 10);
        scaleSlider.addChangeListener(e -> viewModel.onScaleChange(scaleSlider.getValue()));
        viewSettingPanel.add(new JLabel("View Setting"));
        viewSettingPanel.add(scaleSlider);
        viewSettingPanel.setPreferredSize(new Dimension(180, 100));

        springLayout.putConstraint(SpringLayout.SOUTH, viewSettingPanel, -30, SpringLayout.SOUTH, boardView);
        springLayout.putConstraint(SpringLayout.EAST, viewSettingPanel, -30, SpringLayout.EAST, boardView);

        boardView.add(viewSettingPanel);
        gamePanel.add(boardView, BorderLayout.CENTER);

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
