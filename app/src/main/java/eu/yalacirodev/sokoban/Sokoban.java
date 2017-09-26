package eu.yalacirodev.sokoban;


import java.util.Collection;

public interface Sokoban {

    int getCountStep();

    int getCountPush();

    int getHistorySize(); // returns the size of the full history

    Collection<Movement> getHistory(); // the full history

    void trimHistory(); // trims the history to the current steps

    void setHistory(Collection<Movement> c);

    int getCurrentLevel();

    State getCurrentState();

    XSBFile getXSBFile(int level);

    int getCountLevelsLoaded();

    int getPosX();

    int getPosY();

    void setCurrentState(State s);


    Movement undo();

    Movement redo();

    Movement move(Direction dir);

    boolean isWinning();

    void init();

    void reset(); // to the initial state of the current level

    void goToLevel(int n);

    void load(String name); // load a file named *.xsb

    // void load(File[] files); // rajoute plusieurs niveaux


    class Direction {

        final static int UP = 0;
        final static int RIGHT = 1;
        final static int DOWN = 2;
        final static int LEFT = 3;

        final static int LEAR = -17;

        private int dir;
        private int line, column;
        final static Direction NULL = new Direction();

        public Direction() {
            dir = LEAR;
            line = 0;
            column = 0;
        }

        public Direction(int dir) {
            this.dir = dir;
            switch (dir) {
                case RIGHT: {
                    column = 1;
                    line = 0;
                    break;
                }
                case DOWN: {
                    column = 0;
                    line = 1;
                    break;
                }
                case LEFT: {
                    column = -1;
                    line = 0;
                    break;
                }
                case UP: {
                    column = 0;
                    line = -1;
                    break;
                }
            }
        }

        public int moveX() {
            return column;
        }

        public int moveY() {
            return line;
        }

        public int getDirection() {
            return dir;
        }

        public Direction oppositeDirection() {
            if (dir == LEAR)
                return NULL;
            return new Direction((dir + 2) % 4);
        }
    } //end Direction


    class Movement {
        private Direction dir;
        private boolean boxIsPushed;

        public final static Movement NULL = new Movement(Direction.NULL, false);

        public Movement(Direction dir, boolean cP) {
            this.dir = dir;
            boxIsPushed = cP;
        }

        public Direction getDirection() {
            return dir;
        }

        public boolean boxPushed() {
            return boxIsPushed;
        }

        public String toString() {
            return "dir : (" + dir.moveX() + "," + dir.moveY() + ") , boxIsPushed : " + boxIsPushed;
        }

    } // end Movement


}
