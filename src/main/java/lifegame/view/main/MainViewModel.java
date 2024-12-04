package lifegame.view.main;

import lifegame.Main;
import lifegame.component.BoardViewData;
import lifegame.model.GameModel;
import lifegame.util.Point;
import lifegame.util.State;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class MainViewModel {
    AtomicReference<GameModel> gameModel;
    State<Integer> scale = new State<>(10);
    State<Integer> speed = new State<>(5);
    State<Boolean> undoEnabled = new State<>(false);
    State<Boolean> isRunning = new State<>(false);
    State<BoardViewData> board = new State<>(BoardViewData.createEmpty());

    public MainViewModel(GameModel gameModel) {
        this.gameModel = new AtomicReference<>(gameModel);
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
                    gameModel.get().step();
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
        gameModel.get().undo();
        postChange();
    }

    public void onStepClick() {
        gameModel.get().step();
        postChange();
    }

    public void onResetClick() {

    }

    public void onNewGameClick() {
        SwingUtilities.invokeLater(Main::NewWindow);
    }

    public void onBoardClick(Point coord) {
        gameModel.get().changeCellState(coord.x, coord.y, !gameModel.get().getBoardState().getCellState(coord.x, coord.y));
        postChange();
    }

    public void onBoardChange(Point coord, boolean newState) {
        gameModel.get().changeCellState(coord.x, coord.y, newState);
        postChange();
    }

    public void onDispose() {
        disposed = true;
    }

    private void postChange() {
        undoEnabled.setValue(gameModel.get().isUndoEnabled());
        board.setValue(new BoardViewData(gameModel.get().getBoardState().getBoard(), gameModel.get().getBoardState().getStartCoord()));
    }
}
