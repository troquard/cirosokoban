package eu.yalacirodev.sokoban;


import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MySokoban implements Sokoban {


    Context context;
    private ArrayList<XSBFile> initStates;
    private ArrayList<Movement> history;
    private int currentLevel; // 0 is the index of first level
    private int push;
    private int step;
    private int posX;
    private int posY;
    private State currentState;


    public MySokoban(Context context) {
        this.context = context;
        initStates = new ArrayList<>();
        history = new ArrayList<>();
        currentLevel = 0;
        push = 0;
        step = 0;
        posX = -1;
        posY = -1;
    }

    public MySokoban(Sokoban sokoban) {
        currentState = new State(sokoban.getCurrentState());
        push = sokoban.getCountPush();
        step = sokoban.getCountStep();
        history = new ArrayList<>(sokoban.getHistory());
        this.trimHistory();
        posX = currentState.getPosX();
        posY = currentState.getPosY();
    }

    public MySokoban(String name, Context context) {
        this(context);
        load(name);
    }

    public MySokoban(String[] names, Context context) {
        this(context);
        for (String name : names) load(name);
    }

    public void reset() {
        currentState = new State(((XSBFile) initStates.get(currentLevel)).getState());
        posX = currentState.getPosX();
        posY = currentState.getPosY();
        step = 0;
        push = 0;
    }

    public void init() {
        reset();
        history = new ArrayList<>();
    }

    public void goToLevel(int n) {
        if (n >= 0 && n < initStates.size()) {
            currentLevel = n;
            init();
        }
    }

    public void setCurrentState(State s) {
        currentState = new State(s);
        posX = currentState.getPosX();
        posY = currentState.getPosY();
    }

    public int getCountStep() {
        return step;
    }

    public int getCountPush() {
        return push;
    }

    public int getCountLevelsLoaded() {
        return initStates.size();
    }

    public int getHistorySize() {
        return history.size();
    }

    public Collection<Movement> getHistory() {
        return history;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public State getCurrentState() {
        return currentState;
    }

    public XSBFile getXSBFile(int level) {
        if (level < 0 || level > getCountLevelsLoaded())
            return null;
        return (XSBFile) initStates.get(level);
    }


    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setHistory(Collection<Movement> c) {
        reset();
        history = new ArrayList<>(c);
        for (int i = 0; i < history.size() ; i++)
            redo();
    }


    public Movement undo() {
        if (0 < step) {
            step = step - 2;
            Movement mov = (Movement) history.get(step + 1);
            Direction lastDir = mov.getDirection();
            Movement res = genericMove(lastDir.oppositeDirection());
            if (mov.boxPushed()) {
                push--;
                moveBox(posX + 2 * lastDir.moveX(), posY + 2 * lastDir.moveY(), lastDir.oppositeDirection());
            }
            return res;

        } else return null;
    }


    private void moveBox(int x, int y, Direction dir) {
        if (currentState.getBlock(x + dir.moveX(), y + dir.moveY()) == ' ')
            currentState.setBlock(x + dir.moveX(), y + dir.moveY(), '$');
        else  // so it's a dot '.'
            currentState.setBlock(x + dir.moveX(), y + dir.moveY(), '*');

        if (currentState.getBlock(x, y) == '$')
            currentState.setBlock(x, y, ' ');
        else // it's a star '*'
            currentState.setBlock(x, y, '.');
    }


    public Movement redo() {
        if (step < getHistorySize()) {
            Movement mov = (Movement) history.get(step);
            return genericMove(mov.getDirection());
        } else
            return Movement.NULL;
    }

    public void trimHistory() {
        if (step < getHistorySize())
            for (int i = getHistorySize() - 1; i >= step; i--)
                history.remove(i);
    }

    public Movement move(Direction dir) {
        //if (step < getHistorySize())
        //    for (int i = getHistorySize() - 1; i >= step; i--)
        //        history.remove(i);

        trimHistory();

        Movement res = genericMove(dir);
        if (res != Movement.NULL)
            history.add(res);
        return res;
    }


    private Movement genericMove(Direction dir) {
        Movement res;

        boolean boxIsPushed = false;
        char currentPlace = currentState.getBlock(posX, posY); // always @ or +
        char firstPlace = currentState.getBlock(posX + dir.moveX(), posY + dir.moveY());
        char secondPlace;
        if (firstPlace == '#')
            return Movement.NULL;
        else
            secondPlace = currentState.getBlock(posX + 2 * dir.moveX(), posY + 2 * dir.moveY());

        if (possibleTry(firstPlace, secondPlace)) {
            step++;
            currentState.setBlock(posX + dir.moveX(),
                    posY + dir.moveY(),
                    replace(currentPlace, firstPlace));
            currentState.setBlock(posX + 2 * dir.moveX(),
                    posY + 2 * dir.moveY(),
                    replace(firstPlace, secondPlace));
            if (currentPlace == '@')
                currentState.setBlock(posX, posY, ' ');
            else //if it's a +
                currentState.setBlock(posX, posY, '.');

            if (boxIsPushed = ((firstPlace == '$') || (firstPlace == '*')))
                push++;
            res = new Movement(dir, boxIsPushed);

            currentState.setPosition();
            posX = currentState.getPosX();
            posY = currentState.getPosY();

            return res;
        }

        return Movement.NULL;
    }


    private char replace(char c1, char c2) {
        if (c1 == '@' || c1 == '+') { // soko
            if (c2 == ' ' || c2 == '$')
                return '@';
            else // . or *
                return '+';
        } else if (c1 == '$' || c1 == '*') { // box
            if (c2 == ' ')
                return '$';
            else // . or ' '
                return '*';
        } else // empty
            return c2;
    }


    private boolean possibleTry(char a, char b) {
        return !((a == '#') || ((a == '$' || a == '*') && (b == '#' || b == '$' || b == '*')));
    }


    public boolean isWinning() {
        return currentState.isWinning();
    }


    public void load(String name) {

        try {
            initStates.add(XSBReader.read(name, this.context));
        } catch (IOException e) {
            DevTools.DEBUG("IO exception loading " + name);
            e.printStackTrace();
        }
        init();
    }

    /*
    public void load(File[] files) {
        for (File file : files) load(file.getName());
    }
    */


    public String toString() {
        System.out.println("x = " + posX + ", y = " + posY);
        System.out.print("steps = " + step + " ; pushes = " + push);
        return currentState.toString();
    }


}
