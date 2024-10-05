package lifegame.model;

import java.util.ArrayDeque;

public class GameModel {
    private BoardState boardState;
    private ArrayDeque<BoardState> historyStack = new ArrayDeque<>();
    private int historySize = 32;

    public GameModel() {
        this.boardState = new BoardState();
        boardState.randomize();
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    public void step(){
        addHistory();
        boardState = boardState.getNextState();
    }

    private void addHistory(){
        historyStack.push(boardState);
        if(historyStack.size() > historySize){
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
