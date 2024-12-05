package lifegame.model;

import java.util.ArrayDeque;

public class GameModel {
    private BoardState boardState;
    private final ArrayDeque<BoardState> historyStack = new ArrayDeque<>();

    public GameModel() {
        this.boardState = new BoardState();
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public void step(){
        addHistory();
        boardState.nextState();
    }

    public void clear(){
        addHistory();
        this.boardState = new BoardState();
    }

    public void changeCellState(int x, int y, boolean state){
        changeCellState(x, y, state, true);
    }

    public void changeCellState(int x, int y, boolean state, boolean addHistory){
        if(addHistory) addHistory();
        boardState.changeCellState(x, y, state);
    }

    private void addHistory(){
        historyStack.push(boardState.clone());
        int HISTORY_SIZE = 32;
        if(historyStack.size() > HISTORY_SIZE){
            historyStack.pollLast();
        }
    }

    public void undo(){
        if(historyStack.isEmpty()){
            return;
        }
        boardState = historyStack.pop();
    }

    public boolean isUndoEnabled(){
        return !historyStack.isEmpty();
    }
}
