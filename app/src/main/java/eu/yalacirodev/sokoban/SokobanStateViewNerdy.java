package eu.yalacirodev.sokoban;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class SokobanStateViewNerdy extends TextView {


    public SokobanStateViewNerdy(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showState (State state) {
        setText(state.toString());
    }
}
