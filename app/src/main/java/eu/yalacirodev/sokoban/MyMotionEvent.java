package eu.yalacirodev.sokoban;


import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Created by Nicolas Troquard.
 */
public class MyMotionEvent {
    private static float initialX, initialY;

    private static final int CONFIDENCE_X = 10;
    private static final int CONFIDENCE_Y = 10;

    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int EAST = 3;
    public static final int WEST = 4;
    public static final int AMBIGUOUS = 0;
    public static final int SKIP = -1;


    public static int move(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                return SKIP;

            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                float finalY = event.getY();
                return mainDirection(initialX, initialY, finalX, finalY);

            default:
                return SKIP;
        }
    }




    private static int mainDirection(float initX,float initY,float finalX,float finalY) {
        if (Math.abs(initX-finalX) < CONFIDENCE_X && Math.abs(initY-finalY) < CONFIDENCE_Y)
            return AMBIGUOUS; // ambiguous direction

        if (Math.abs(initX-finalX) > Math.abs(initY-finalY)) {
            if (initX > finalX)
                return WEST; // left
            else
                return EAST; // right
        }
        if (Math.abs(initX-finalX) < Math.abs(initY-finalY)) {
            if (initY > finalY)
                return NORTH; // up
            else
                return SOUTH; // down
        }

        return AMBIGUOUS; // ambiguous direction
    }
}
