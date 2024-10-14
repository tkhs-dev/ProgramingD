package lifegame.view.main;

import lifegame.component.BoardViewData;
import lifegame.model.GameModel;
import lifegame.util.Point;
import lifegame.util.State;

public class MainViewModel {
    GameModel gameModel;
    State<Integer> scale = new State<>(10);
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

    public void onStartClick() {
        isRunning.setValue(!isRunning.getValue());
    }

    public void onUndoClick() {
        gameModel.undo();
        undoEnabled.setValue(gameModel.isUndoEnabled());
        board.setValue(new BoardViewData(gameModel.getBoardState().getBoard(), gameModel.getBoardState().getStartCoord()));
    }

    public void onStepClick() {
        gameModel.step();
        undoEnabled.setValue(gameModel.isUndoEnabled());
        board.setValue(new BoardViewData(gameModel.getBoardState().getBoard(), gameModel.getBoardState().getStartCoord()));
    }

    public void onResetClick() {

    }

    public void onNewGameClick() {

    }

    public void onBoardClick(Point coord) {
        gameModel.changeCellState(coord.x, coord.y, !gameModel.getBoardState().getCellState(coord.x, coord.y));
        undoEnabled.setValue(gameModel.isUndoEnabled());
        board.setValue(new BoardViewData(gameModel.getBoardState().getBoard(), gameModel.getBoardState().getStartCoord()));
    }

    public void onBoardChange(Point coord, boolean newState) {
        gameModel.changeCellState(coord.x, coord.y, newState);
        undoEnabled.setValue(gameModel.isUndoEnabled());
        board.setValue(new BoardViewData(gameModel.getBoardState().getBoard(), gameModel.getBoardState().getStartCoord()));
    }
}
