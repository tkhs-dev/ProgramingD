package lifegame.view.main;

import lifegame.Main;
import lifegame.component.BoardViewData;
import lifegame.model.GameModel;
import lifegame.util.Point;
import lifegame.util.State;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class MainViewModel {
    GameModel gameModel;
    State<Integer> scale = new State<>(10);
    State<Integer> speed = new State<>(5);
    State<Boolean> undoEnabled = new State<>(false);
    State<Boolean> isRunning = new State<>(false);
    State<BoardViewData> board = new State<>(BoardViewData.createEmpty());

    public MainViewModel(GameModel gameModel) {
        this.gameModel = gameModel;
        undoEnabled.setValue(gameModel.isUndoEnabled());
    }

    public void onScaleChange(Integer scale) {
        this.scale.setValue(scale);
    }

    public void onSpeedChange(Integer speed) {
        this.speed.setValue(speed);
    }

    private boolean disposed = false;
    public void onStartClick() {
        if (isRunning.getValue()) {
            isRunning.setValue(false);
        } else {
            isRunning.setValue(true);
            new Thread(() -> {
                while (isRunning.getValue() && !disposed) {
                    gameModel.step();
                    postChange();
                    try {
                        Thread.sleep(1100 - 100L * speed.getValue());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void onUndoClick() {
        gameModel.undo();
        postChange();
    }

    public void onStepClick() {
        gameModel.step();
        postChange();
    }

    public void onResetClick() {
        gameModel.clear();
        postChange();
    }

    public void onNewGameClick() {
        SwingUtilities.invokeLater(Main::NewWindow);
    }

    public void onBoardClick(Point coord) {
        gameModel.changeCellState(coord.x, coord.y, !gameModel.getBoardState().getCellState(coord.x, coord.y));
        postChange();
    }

    public void onBoardChange(Point coord, boolean newState) {
        gameModel.changeCellState(coord.x, coord.y, newState);
        postChange();
    }

    public void onDispose() {
        disposed = true;
    }

    private void postChange() {
        undoEnabled.setValue(gameModel.isUndoEnabled());
        board.setValue(new BoardViewData(gameModel.getBoardState().getBoard(), gameModel.getBoardState().getStartCoord()));
    }

    public void saveState() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("盤面ファイル", "lg");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            gameModel.saveState(fileChooser.getSelectedFile());
        }
    }

    public void loadState() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("盤面ファイル", "lg");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            loadState(fileChooser.getSelectedFile());
        }
    }

    public void loadState(File file) {
        gameModel.loadState(file);
        postChange();
    }
}
