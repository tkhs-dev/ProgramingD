package lifegame.model;

import lifegame.Main;

import javax.swing.*;
import java.io.*;
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

    public synchronized void step(){
        addHistory();
        boardState.nextState();
    }

    public synchronized void clear(){
        addHistory();
        this.boardState = new BoardState();
    }

    public synchronized void changeCellState(int x, int y, boolean state){
        changeCellState(x, y, state, true);
    }

    public synchronized void changeCellState(int x, int y, boolean state, boolean addHistory){
        if(addHistory) addHistory();
        boardState.changeCellState(x, y, state);
    }

    private synchronized void addHistory(){
        historyStack.push(boardState.clone());
        int HISTORY_SIZE = 32;
        if(historyStack.size() > HISTORY_SIZE){
            historyStack.pollLast();
        }
    }

    public synchronized void undo(){
        if(historyStack.isEmpty()){
            return;
        }
        boardState = historyStack.pop();
    }

    public boolean isUndoEnabled(){
        return !historyStack.isEmpty();
    }

    public void saveState(File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            boardState.writeExternal(objectOutputStream);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to save state");
        }
    }

    public void loadState(File file) {
        InputStream fileInputStream = null;
        try {
            if(file.getPath().startsWith("/presets")){
                fileInputStream = getClass().getResourceAsStream(file.getPath().replace("\\", "/"));
            }else {
                fileInputStream = new FileInputStream(file);
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            boardState.readExternal(objectInputStream);
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found");
        } catch(StreamCorruptedException e){
            JOptionPane.showMessageDialog(null, "Invalid file format");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to load state");
        }
    }
}
