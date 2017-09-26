package eu.yalacirodev.sokoban;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class LevelSelectionAdapter extends BaseAdapter {

    private Context context;
    private MySokobanModel sokoban;
    private DatabaseHelper levelsDB;

    public LevelSelectionAdapter(Context context, MySokobanModel sokoban, DatabaseHelper levelsDB) {
        this.context = context;
        this.sokoban = sokoban;
        this.levelsDB = levelsDB;
    }

    @Override
    public int getCount() {
        return sokoban.getCountLevelsLoaded();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LevelView lv;

        if (convertView == null) {
            lv = new LevelView(context, null);
        } else {
            lv = (LevelView) convertView;
        }

        int realNumLevel = position + 1;
        int bestRes = levelsDB.getNumMoves(position);
        int levelRecord = sokoban.getXSBFile(position).getBestStep();
        String stars = (bestRes == DatabaseHelper.NOT_COMPLETED)?"":DevTools.stars(bestRes, levelRecord);
        String bestSteps = (bestRes == DatabaseHelper.NOT_COMPLETED) ? "" : "(" + bestRes + ")";

        if (bestRes == DatabaseHelper.NOT_COMPLETED)
            lv.setColor(R.color.colorPrimaryDark);
        else
            lv.setColor(R.color.colorPrimary);

        lv.setDescription(context.getResources().getString(R.string.level) + " " + realNumLevel);
        lv.setScore(stars + " " + bestSteps);
        int zoom = Math.max(sokoban.getXSBFile(position).getState().getWidth(),
                sokoban.getXSBFile(position).getState().getHeight());
        lv.setLevel(sokoban.getXSBFile(position).getState(),
                zoom,
                sokoban.getXSBFile(position).getBestStep());

        return lv;
    }

    private class LevelView extends RelativeLayout {
        private SokobanStateViewNice level;
        private TextView description;
        private TextView score;
        private Context context;


        public LevelView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;

            initViews(context, attrs);
        }

        public LevelView(Context context, AttributeSet attrs,  int defStyle) {
            super(context, attrs, defStyle);
            this.context = context;

            initViews(context, attrs);
        }

        private void initViews(Context context, AttributeSet attrs) {

            LayoutInflater mInflater = LayoutInflater.from(context);
            mInflater.inflate(R.layout.level_select_layout, this);

            description = (TextView) findViewById(R.id.level_description);
            description.setTextColor(ContextCompat.getColor(context, R.color.white));
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            score = (TextView) findViewById(R.id.level_score);
            score.setTextColor(ContextCompat.getColor(context, R.color.white));
            score.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            level = (SokobanStateViewNice) findViewById(R.id.level);
            level.setMinimumHeight(context.getResources().getDisplayMetrics().heightPixels / 5);
        }

        public void setLevel(State state, int zoom, int levelBest) {
            level.showState(state, 0,0, zoom, levelBest);
        }

        public void setDescription(String text) {
            description.setText(text);
        }

        public void setScore(String text) {
            score.setText(text);
        }

        public void setColor(int colorid) {
            int color = ContextCompat.getColor(context, colorid);
            this.setBackgroundColor(color);
        }
    }
}
