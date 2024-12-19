package lifegame.model;

import lifegame.Main;

import javax.swing.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReference;

public class GameModel {
    private AtomicReference<BoardState> boardState;
    private final Deque<BoardState> historyStack = new ArrayDeque<>();

    public GameModel() {
        this.boardState = new AtomicReference<>(new BoardState());
    }

    public BoardState getBoardState() {
        return boardState.get();
    }

    public synchronized void step(){
        addHistory();
        boardState.get().nextState();
    }

    public synchronized void clear(){
        addHistory();
        this.boardState.set(new BoardState());
    }

    public synchronized void changeCellState(int x, int y, boolean state){
        changeCellState(x, y, state, true);
    }

    public synchronized void changeCellState(int x, int y, boolean state, boolean addHistory){
        if(addHistory) addHistory();
        boardState.get().changeCellState(x, y, state);
    }

    private synchronized void addHistory(){
        historyStack.push(boardState.get().clone());
        int HISTORY_SIZE = 32;
        if(historyStack.size() > HISTORY_SIZE){
            historyStack.pollLast();
        }
    }

    public synchronized void undo(){
        if(historyStack.isEmpty()){
            return;
        }
        boardState.set(historyStack.pop());
    }

    public boolean isUndoEnabled(){
        return !historyStack.isEmpty();
    }

    public void saveState(File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            boardState.get().writeExternal(objectOutputStream);
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
            boardState.get().readExternal(objectInputStream);
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
