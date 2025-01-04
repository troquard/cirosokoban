package eu.yalacirodev.sokoban;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class SokobanStateViewASCII extends TextView {

    /*String[] soko = {
            "(\\___/)", "(='.'=)", "(\") (\")"};*/
    String[] vide = {
            "       ", "       ", "       "};
    /*String[] sokoplace = {
            "(\\___/)", "(=O_o=)", "(\") (\")"};*/
    String[] sokoplace = {
            "(\\___/)", "(='x'=)", "(\") (\")"};
    String[] caisse = {
            " 00000 ", "0000000", " 00000 "};
    String[] rangement = {
            " \\   / ", "|- X -|", " /   \\ "};
    String[] caisseplace = {
            " 00000 ", "0- X -0", " 00000 "};
    String[] mur = {
            "MMMMMMM", "MMMMMMM", "MMMMMMM"};
    String[] sokofin = {
            "(\\___/)", "(=^.^=)", "(\") (\")"};

    String[] sokoright = {
            "(\\__/) ", "( '.') ", "c(\")(\")"};
    String[] sokoleft = {
            " (\\__/)", " ('.' )", "(\")(\")o"};

    private boolean sokoGoesLeft;
    // old positions used to decide whether soko goes left
    private int oldPosX = -1767; //dummy
    private int oldPosY = -1768; //dummy


    public SokobanStateViewASCII(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void showState (State state) {

        StringBuilder toprint = new StringBuilder();

        int sokoX = state.getPosX();
        int sokoY = state.getPosY();

        //this.sokoGoesLeft = sokoGoesLeft;
        this.sokoGoesLeft = (sokoY != oldPosY)?this.sokoGoesLeft:(oldPosX > sokoX);
        oldPosX = sokoX;
        oldPosY = sokoY;


        int minX, maxX, minY, maxY;

        if (state.getWidth() <= 8) {
            minX = 0;
            maxX = state.getWidth();
        } else {
            if (sokoX < 4) {
                minX = 0;
                maxX = Math.min(state.getWidth(), 8);
            }
            else if (sokoX > state.getWidth() - 5) { // was 4
                minX = Math.max(0,state.getWidth() - 8); // was 7
                maxX = state.getWidth();
            }
            else {
                minX = Math.max(0,sokoX - 3); // was 3
                maxX = Math.min(state.getWidth(),sokoX + 5);
            }
        }
        if (state.getHeight() <= 8) {
            minY = 0;
            maxY = state.getHeight();
        } else {
            if (sokoY < 4) {
                minY = 0;
                maxY = Math.min(state.getHeight(), 8);
            }
            else if (sokoY > state.getHeight() - 5) { // was 4
                minY = Math.max(0,state.getHeight() - 8); // was 7
                maxY = state.getHeight();
            }
            else {
                minY = Math.max(0, sokoY - 3); // was 3
                maxY = Math.min(state.getHeight(), sokoY + 5);
            }
        }


        for (int i = minY; i < maxY; i = i + 1) {
            for (int k = 0; k < 3; k = k + 1) {
                for (int j = minX; j < maxX; j = j + 1) {

                    switch (state.getBlock(j, i)) {
                        case ' ':
                            toprint.append(vide[k]);// = toprint + vide[k];
                            break;
                        case '#':
                            toprint.append(mur[k]);// = toprint + mur[k];
                            break;
                        case '$':
                            toprint.append(caisse[k]);// = toprint + caisse[k];
                            break;
                        case '.':
                            toprint.append(rangement[k]);// = toprint + rangement[k];
                            break;
                        case '*':
                            toprint.append(caisseplace[k]);// = toprint + caisseplace[k];
                            break;
                        case '@':
                            if (state.isWinning())
                                toprint.append(sokofin[k]);// = toprint + sokofin[k];
                            else if (sokoGoesLeft)
                                toprint.append(sokoleft[k]);// = toprint + soko[k];
                            else
                                toprint.append(sokoright[k]);
                            break;
                        case '+':
                            toprint.append(sokoplace[k]);// = toprint + sokoplace[k];
                            break;
                    }
                }
                if (i < maxY - 1|| k != 2)
                    toprint = toprint.append("\n");// + "\n";
            }
        }
        this.setText(toprint);


    }
}
