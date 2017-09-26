package eu.yalacirodev.sokoban;


public class State {

    private char[][] state;
    private int width;
    private int height;
    private int posX;
    private int posY;

    // Construct a State from the char matrix.
    public State (char[][] state) {
        this(state.length, state[0].length);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                setBlock(x, y, state[y][x]);
    }

    // Clone a State. Construct a State from a State.
    public State (State s) {
        this(s.getHeight(), s.getWidth());
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                setBlock(x, y, s.getBlock(x, y));
        setPosition();
    }

    // Create an empty State with dimensions height and width.
    // posX and posY remain non instantiated.
    public State (int height, int width) {
        state = new char[height][width];
        this.height = height;
        this.width = width;
    }

    // Find the position of the soko and accordingly set posX and posY.
    public void setPosition() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (state[i][j] == '@' || state[i][j] == '+') {
                    posY = i;
                    posX = j;
                }
            }
    }

    // set posX and posY.
    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    // Get the matrix representation of the State
    public char[][] getState() {
	return state;
    }

    // Set a char at the (x,y) coordinate
    public void setBlock(int x, int y, char c) {
        assert (y < height && x < width && x >= 0 && y >= 0);
        state[y][x] = c;
    }

    // Get the char currently at the (x,y) coordinate
    public char getBlock(int x, int y) {
        assert (y < height && x < width && x >= 0 && y >= 0);
        return state[y][x];
    }

    public int getHeight() {
	return height;
    }

    public int getWidth() {
	return width;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    // Determines whether the current state is winning.
    public boolean isWinning() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (state[i][j] == '.' || state[i][j] == '+')
                    return false;
        return true;
    }

    public String toString () {
        StringBuffer s = new StringBuffer();
        s.append("\n");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                s.append(state[i][j]);
            s.append("\n");
        }
        //s.append("rows : " + height + ", columns : " + width + "\n");
        return s.toString();
    }

}
