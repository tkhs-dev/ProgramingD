package lifegame.view.main;

import lifegame.component.BoardViewData;
import lifegame.model.BoardModel;
import lifegame.util.State;

public class MainViewModel {
    BoardModel boardModel;
    State<Integer> scale = new State<>(10);
    State<Boolean> undoEnabled = new State<>(false);
    State<Boolean> isRunning = new State<>(false);
    State<BoardViewData> board = new State<>(BoardViewData.createEmpty());

    public MainViewModel(BoardModel boardModel) {
        this.boardModel = boardModel;
        boardModel.randomize();
    }

    public void onScaleChange(Integer scale) {
        System.out.println("Scale changed to " + scale);
        this.scale.setValue(scale);
    }

    public void onStartClick() {
        isRunning.setValue(!isRunning.getValue());
    }

    public void onUndoClick() {

    }

    public void onStepClick() {
        boardModel.step();
    }

    public void onResetClick() {

    }

    public void onNewGameClick() {

    }
}
