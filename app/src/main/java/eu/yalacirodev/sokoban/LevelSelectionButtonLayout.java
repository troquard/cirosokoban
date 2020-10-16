package eu.yalacirodev.sokoban;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class LevelSelectionButtonLayout extends LinearLayout {

    Context context;

    public LevelSelectionButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    public void actionSelectLevel(MySokobanModel sokoban, DatabaseHelper levelsDB, AlertDialog ad) {

        final MySokobanModel finalSokoban = sokoban;
        final AlertDialog finalAd = ad;

        ScrollView scrollView = new ScrollView(context);
        LinearLayout layoutOfManyButtons = new LinearLayout(context);
        // this is a linear layout with centered gravity
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        // a scroll view is inside
        this.addView(scrollView);
        // a linear layout is inside and will contain many buttons
        scrollView.addView(layoutOfManyButtons);
        layoutOfManyButtons.setOrientation(LinearLayout.VERTICAL);


        // add and configure one button for every level loaded
        for (int i = 0; i < sokoban.getCountLevelsLoaded(); i++) {
            final int numLevel = i + 1;
            Button btnDes = new Button(context);

            int bestRes = levelsDB.getNumMoves(i);
            String bestSteps = (bestRes==DatabaseHelper.NOT_COMPLETED)?"(??)":"(" + bestRes + ")";

            btnDes.setGravity(Gravity.FILL_HORIZONTAL);
            btnDes.setText("[" + numLevel + "] " + sokoban.getXSBFile(i).getTitle() + " " + bestSteps);
            btnDes.setTextColor(ContextCompat.getColor(context, R.color.white));

            if (bestRes==DatabaseHelper.NOT_COMPLETED)
                btnDes.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            else
                btnDes.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));


            btnDes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finalSokoban.goToLevel(numLevel - 1);
                    finalAd.dismiss();
                }
            });


            layoutOfManyButtons.addView(btnDes);
        }
    }
}
