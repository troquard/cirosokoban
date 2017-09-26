package eu.yalacirodev.sokoban;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;


public class SokobanStateViewNice extends View {
    private Paint paint;
    private TextPaint textPaint;
    private int w;
    private int h;
    private State state;
    private int steps;
    private int pushes;
    private int levelbest;
    private boolean sokoGoesLeft;
    // old positions used to decide whether soko goes left
    private int oldPosX;
    private int oldPosY;

    private int maxElements;
    private final int DEFAULT_MAX_ELEMENTS = 8;


    private Drawable e_wall = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_wall_alpha, null);
    private Drawable e_boulder = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_boulder, null);
    private Drawable e_target = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_target, null);
    private Drawable e_boulder_on_target = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_boulder_on_target, null);
    private Drawable e_bunny_win = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_bunny_win, null);
    private Drawable e_bunny_left = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_bunny_left, null);
    private Drawable e_bunny_right = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_bunny_right, null);
    private Drawable e_bunny_on_target = ResourcesCompat.getDrawable(getResources(), R.drawable.ascii_bunny_on_target, null);

    public SokobanStateViewNice(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);

        textPaint = new TextPaint();
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccentLight));

        maxElements = DEFAULT_MAX_ELEMENTS;
        oldPosX = -1767; //dummy
        oldPosY = -1768; //dummy
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int sizeElement = (int) Math.floor(Math.min(getWidth(),getHeight()) / maxElements);

        int sokoX = state.getPosX();
        int sokoY = state.getPosY();

        int numMaxElementsX = (int) Math.floor(getWidth() / sizeElement);
        int numMaxElementsY = (int) Math.floor(getHeight() / sizeElement);

        int minX, maxX, minY, maxY;

        if (state.getWidth() <= numMaxElementsX) {
            minX = 0;
            maxX = state.getWidth();
        } else {
            if (sokoX < numMaxElementsX / 2) {
                minX = 0;
                maxX = Math.min(state.getWidth(), numMaxElementsX + 1);
            }
            else if (sokoX > state.getWidth() - ((numMaxElementsX / 2) + 2)) { // was 4
                minX = Math.max(0,state.getWidth() - (numMaxElementsX + 1)); // was 7
                maxX = state.getWidth();
            }
            else {
                minX = Math.max(0,sokoX - ((numMaxElementsX / 2) + 0)); // was -1
                maxX = Math.min(state.getWidth(),sokoX + ((numMaxElementsX / 2) + 2));
            }
        }
        if (state.getHeight() <= numMaxElementsY) {
            minY = 0;
            maxY = state.getHeight();
        } else {
            if (sokoY < numMaxElementsY / 2) {
                minY = 0;
                maxY = Math.min(state.getHeight(), numMaxElementsY + 1);
            }
            else if (sokoY > state.getHeight() - ((numMaxElementsY / 2) + 2)) { // was 4
                minY = Math.max(0,state.getHeight() - (numMaxElementsY + 1)); // was 7
                maxY = state.getHeight();
            }
            else {
                minY = Math.max(0, sokoY - ((numMaxElementsY / 2) + 0)); // was -1
                maxY = Math.min(state.getHeight(), sokoY + ((numMaxElementsY / 2) + 2));
            }
        }




        boolean numElementsXisOdd = ((maxX - minX) % 2) == 1;
        boolean numElementsYisOdd = ((maxY - minY) % 2) == 1;
        for (int j = minX; j < maxX; j = j + 1) {
            for (int i = minY; i < maxY; i = i + 1) {

                Drawable element;
                int x,y;
                x = Math.round(w / 2) - sizeElement*(Math.round((maxX - minX) /2)) + sizeElement*(j - minX)
                        - (numElementsXisOdd?Math.round(sizeElement / 2):0);
                y = Math.round(h / 2) - sizeElement*(Math.round((maxY - minY) /2)) + sizeElement*(i - minY)
                        - (numElementsYisOdd?Math.round(sizeElement / 2):0);


                switch (state.getBlock(j, i)) {
                    case ' ':
                        break;
                    case '#':
                        element = e_wall;
                        element.setBounds(x, y, x+sizeElement, y+sizeElement);
                        element.draw(canvas);
                        break;
                    case '$':
                        element = e_boulder;
                        element.setBounds(x, y, x+sizeElement, y+sizeElement);
                        element.draw(canvas);
                        break;
                    case '.':
                        element = e_target;
                        element.setBounds(x, y, x+sizeElement, y+sizeElement);
                        element.draw(canvas);
                        break;
                    case '*':
                        element = e_boulder_on_target;
                        element.setBounds(x, y, x+sizeElement, y+sizeElement);
                        element.draw(canvas);
                        break;
                    case '@':
                        element = state.isWinning()?e_bunny_win:(sokoGoesLeft?e_bunny_left:e_bunny_right);
                        element.setBounds(x, y, x+sizeElement, y+sizeElement);
                        element.draw(canvas);
                        break;
                    case '+':
                        element = e_bunny_on_target;
                        element.setBounds(x, y, x+sizeElement, y+sizeElement);
                        element.draw(canvas);
                        break;

                }



            }
        }

        if (state.isWinning()) {
            String info = steps + " " + getResources().getString(R.string.steps).toUpperCase() + " ; "
                    + pushes + " " + getResources().getString(R.string.pushes).toUpperCase();
            String finished = getResources().getString(R.string.finished);
            String textOnCanvas = finished + "\n" +
                    info + "\n" + DevTools.stars(steps, levelbest);

            DevTools.setTextSizeForWidth(textPaint, 2 * w / 3, info);

            StaticLayout sl = new StaticLayout(textOnCanvas, textPaint, w,
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);

            int slheight = sl.getHeight();
            int slwidth = sl.getWidth();

            Rect rect = new Rect((w / 2) - (slwidth /2), 0, (w / 2) + (slwidth /2), slheight);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            //paint.setStrokeWidth(10);
            canvas.drawRect(rect, paint);
            sl.draw(canvas);
        }
    }


    public void showState (State state, int numSteps, int numPushes, int maxElements, int levelbest) {
        this.state = state;
        this.maxElements = maxElements;
        this.levelbest = levelbest;

        int newPosX = state.getPosX();
        int newPosY = state.getPosY();
        this.sokoGoesLeft = (newPosY != oldPosY)?this.sokoGoesLeft:(oldPosX > newPosX);
        oldPosX = newPosX;
        oldPosY = newPosY;

        steps = numSteps;
        pushes = numPushes;

        invalidate();
    }

}
