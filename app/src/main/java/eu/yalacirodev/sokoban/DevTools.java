package eu.yalacirodev.sokoban;


import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class DevTools {

    private static boolean _DEBUG = false;
    private static String DEBUG_TAG = "SOKOBAN_DEBUG";
    private static String FORCE_DEBUG_TAG = "SOKOBAN_FORCE_DEBUG";

    public static void DEBUG(String string) {
        if (_DEBUG) Log.d(DEBUG_TAG, string);
    }

    public static void FORCE_DEBUG(String string) {
        Log.d(FORCE_DEBUG_TAG, string);
    }


    private static final String NO_STARS = "";
    private static final String ONE_STAR = "\u2605";
    private static final String TWO_STARS = "\u2605\u2605";
    private static final String THREE_STARS = "\u2605\u2605\u2605";
    private static final String FOUR_STARS = "\u2605\u2605\u2605\u2605";

    public static String stars(int moves, int levelbest) {

        String stars = NO_STARS;
        if (moves <= levelbest * 2)
            stars = ONE_STAR;
        if (moves <= 3 * levelbest / 2)
            stars = TWO_STARS;
        if (moves <= 5 * levelbest / 4)
            stars = THREE_STARS;
        if (moves <= levelbest)
            stars = FOUR_STARS;
        return stars;
    }


    /**
     * This is taken from Michael Scheper's answer on stackoverflow
     * http://stackoverflow.com/a/21895626.
     *
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint
     *            the Paint to set the text size for
     * @param desiredWidth
     *            the desired width
     * @param text
     *            the text that should be that width
     */
    public static void setTextSizeForWidth(Paint paint, float desiredWidth, String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }
}
