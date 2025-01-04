package eu.yalacirodev.sokoban;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class MySokobanModel {

    private Sokoban sokoban;
    private LinkedList<SokobanListener> listenerSet;

    public MySokobanModel(Sokoban sokoban) {
        this.sokoban = sokoban;
        listenerSet = new LinkedList<>();
    }

    public void addSokobanListener(SokobanListener listener)
    {
        listenerSet.add(listener);
    }

    public void removeSokobanListener(SokobanListener listener)
    {
        listenerSet.remove(listener);
    }

    public Sokoban getSokoban()
    {
        return sokoban;
    }

    public int getCountStep(){
        return sokoban.getCountStep();
    }

    public int getCountPush(){
        return sokoban.getCountPush();
    }

    public int getHistorySize(){
        return sokoban.getHistorySize();
    }

    public Collection<Sokoban.Movement> getHistory(){
        return sokoban.getHistory();
    }

    public void trimHistory() { sokoban.trimHistory(); };

    public void setHistory(Collection<Sokoban.Movement> c) { sokoban.setHistory(c); }

    public int getCurrentLevel(){
        return sokoban.getCurrentLevel();
    }

    public State getCurrentState(){
        return sokoban.getCurrentState();
    }

    public int getCountLevelsLoaded(){
        return sokoban.getCountLevelsLoaded();
    }

    public XSBFile getXSBFile(int level){
        return sokoban.getXSBFile(level);
    }

    public int getPosX(){
        return sokoban.getPosX();
    }

    public int getPosY(){
        return sokoban.getPosY();
    }

    public boolean isWinning(){
        return sokoban.isWinning();
    }

    public void load(String name) {
        sokoban.load(name);
    }

    public void setCurrentState(State s){
        sokoban.setCurrentState(s);
    }

    public Sokoban.Movement move(Sokoban.Direction dir) {
        Sokoban.Movement movement = sokoban.move(dir);

        if (movement != null && movement != Sokoban.Movement.NULL)
            notifyMove(movement.getDirection());
        return movement;
    }

    public Sokoban.Movement undo() {
        Sokoban.Movement movement = sokoban.undo();

        if (movement != null && movement != Sokoban.Movement.NULL)
            notifyMove(movement.getDirection());
        return movement;
    }

    public Sokoban.Movement redo() {
        Sokoban.Movement movement = sokoban.redo();

        if (movement != null && movement != Sokoban.Movement.NULL)
            notifyMove(movement.getDirection());
        return movement;
    }

    private void notifyMove(Sokoban.Direction dir) {
        Iterator i = listenerSet.listIterator(0);
        while (i.hasNext()) {
            SokobanListener listener = (SokobanListener) i.next();
            listener.sokobanOnMove(dir);
        }
    }

    public void reset() {
        sokoban.reset();
        notifyReset();
    }

    private void notifyReset() {
        Iterator i = listenerSet.listIterator(0);
        while (i.hasNext()) {
            SokobanListener listener = (SokobanListener) i.next();
            listener.sokobanOnReset();
        }
    }

    public void init() {
        sokoban.init();
        notifyInit();
    }

    private void notifyInit() {
        Iterator i = listenerSet.listIterator(0);
        while (i.hasNext()) {
            SokobanListener listener = (SokobanListener) i.next();
            listener.sokobanOnInit();
        }
    }


    public void goToLevel(int n) {
        sokoban.goToLevel(n);
        notifyGoToLevel();
    }

    private void notifyGoToLevel() {
        Iterator i = listenerSet.listIterator(0);
        while (i.hasNext()) {
            SokobanListener listener = (SokobanListener) i.next();
            listener.sokobanOnGoToLevel();
        }

    }
}