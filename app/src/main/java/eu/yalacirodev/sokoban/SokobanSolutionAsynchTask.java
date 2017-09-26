package eu.yalacirodev.sokoban;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Collection;


public class SokobanSolutionAsynchTask extends AsyncTask<Void, Integer, Void>
        implements SokobanSolution.SokobanSolutionListener {

        private MainActivity context;
        private ProgressDialog progressDialog;
        private Sokoban sokoban;
        private MySokobanModel mSokoban;
        private SokobanSolution ss;
        private Collection<Sokoban.Movement> history;
        private boolean FLAG_CANCELLED;
        private final int NUM_STEPS_HINT = 100000;
        private final int TIME_BTW_FRAMES = 120;

        private class MyProgressDialog extends ProgressDialog { // TODO use or delete

            public MyProgressDialog(Context context) {
                super(context);
                this.setIndeterminate(true);
                this.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.soko_animated_left_right, null));
            }

        }

        public SokobanSolutionAsynchTask(MySokobanModel mSokoban, MainActivity context) {
            this.context = context;
            this.mSokoban = mSokoban;
            sokoban = mSokoban.getSokoban();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            sokoban.trimHistory();
            history = new ArrayList<>(sokoban.getHistory());
            FLAG_CANCELLED = false;

            progressDialog = new ProgressDialog(context);

            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.soko_animated_left_right, null));

            progressDialog.setTitle(R.string.text_title_solving);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(context.getResources().getString(R.string.text_message_solving));

            progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,
                    context.getResources().getString(R.string.text_icon_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            ss.cancel();
                            FLAG_CANCELLED = true;
                            mSokoban.setHistory(history);
                        }});

            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params){
            ss = new SokobanSolution(sokoban, NUM_STEPS_HINT);
            ss.addSokobanSolutionListener(this);
            ss.run();

            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            //progressDialog.setTitle("Solving...");
            String info = context.getString(R.string.looking_at_solutions_with) + " "
                    + progress[0] + " " + context.getString(R.string.steps) + ".";
            progressDialog.setMessage(info);

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ss.removeSokobanSolutionListener(this);

            if (FLAG_CANCELLED)
                return;

            progressDialog.cancel();

            if (mSokoban.getCountStep() < mSokoban.getHistorySize()) {
                // There's a solution
                progressDialog = new ProgressDialog(context);

                progressDialog.setIndeterminate(true);
                progressDialog.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.soko_animated_happy, null));

                progressDialog.setTitle(R.string.text_title_solved);
                progressDialog.setMessage(context.getResources().getString(R.string.text_message_solved));
                progressDialog.setCancelable(false);
                progressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                        context.getResources().getString(R.string.text_icon_showme),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                context.setStateHadHelp(true);

                                (new Thread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        while (mSokoban.getCountStep() < mSokoban.getHistorySize()) {
                                            //if (Thread.currentThread().isAlive()) // TODO Check this
                                            //    DevTools.FORCE_DEBUG("ALIVE");

                                            try {
                                                Thread.sleep(TIME_BTW_FRAMES);
                                                ((MainActivity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        DevTools.DEBUG("HIST SIZE "
                                                                + mSokoban.getHistorySize()
                                                                + " STEP COUNT "
                                                                + mSokoban.getCountStep());
                                                        Sokoban.Movement m = mSokoban.redo();
                                                        DevTools.DEBUG("Number of steps done in animation "
                                                                + mSokoban.getCountStep());
                                                    }
                                                });
                                            } catch (InterruptedException e) {
                                                System.err.print("Stopped animation thread : " + e);
                                            }
                                        }
                                    }
                                })).start();
                            }
                        });

                progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,
                        context.getResources().getString(R.string.text_icon_no_thanks), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mSokoban.setHistory(history);
                            }});
            }
            else {
                progressDialog = new ProgressDialog(context);

                progressDialog.setIndeterminate(true);
                progressDialog.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.soko_animated_left_right, null));

                progressDialog.setTitle(R.string.text_title_no_solution);
                progressDialog.setMessage(context.getResources().getString(R.string.text_message_no_solution));
                progressDialog.setCancelable(false);
                progressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                        context.getResources().getText(R.string.text_icon_sad_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mSokoban.setHistory(history);
                            }});
            }

            progressDialog.show();
        }

    @Override
    public void solvingStateChange() {

    }

    @Override
    public void solvingSolutionRankChange() {
        publishProgress(ss.currentSolutionRank());
    }
}
