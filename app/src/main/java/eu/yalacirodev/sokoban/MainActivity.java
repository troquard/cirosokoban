package eu.yalacirodev.sokoban;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity implements SokobanListener {

    protected static final String STATE_CURRENT_LEVEL = "state current level";
    protected static final String STATE_ZOOM = "state zoom";
    protected static final String STATE_HISTORY = "state history";
    protected static final String STATE_HAD_HELP = "state had help";

    private int DEFAULT_MAX_ELEMENTS = 8;

    private Menu mMenu;
    private MySokobanModel mSokoban;
    private int zoomMaxNumDisplayedElements;
    private boolean hadHelp; // sneaky user used help


    private SokobanStateViewNice myDisplay;
    //private SokobanStateViewASCII myDisplay;

    private static final String[] LEVELS = {
            //"9x9-1.xsb",
            "7x7-1.xsb", "7x7-2.xsb", "7x7-3.xsb", "7x7-4.xsb", "7x7-5.xsb",
            //"soko-no-sol.xsb", "easy.xsb",
            "novoban01.xsb", "novoban02.xsb", "novoban03.xsb", "novoban04.xsb", "novoban05.xsb",
            "novoban06.xsb", "novoban07.xsb", "novoban08.xsb", "novoban09.xsb", "novoban10.xsb",
            "novoban11.xsb", "novoban12.xsb", "novoban13.xsb", "novoban14.xsb", "novoban15.xsb",
            "novoban16.xsb", "novoban17.xsb", "novoban18.xsb", "novoban19.xsb", "novoban20.xsb",
            "novoban21.xsb", "novoban22.xsb", "novoban23.xsb", "novoban24.xsb", "novoban25.xsb",
            "novoban26.xsb", "novoban27.xsb", "novoban28.xsb", "novoban29.xsb", "novoban30.xsb",
            "novoban31.xsb", "novoban32.xsb", "novoban33.xsb", "novoban34.xsb", "novoban35.xsb",
            "novoban36.xsb", "novoban37.xsb", "novoban38.xsb", "novoban39.xsb", "novoban40.xsb",
            "novoban41.xsb", "novoban42.xsb", "novoban43.xsb", "novoban44.xsb", "novoban45.xsb",
            "novoban46.xsb", "novoban47.xsb", "novoban48.xsb", "novoban49.xsb", "novoban50.xsb",
            "sokolate01.xsb", "sokolate02.xsb", "sokolate03.xsb", "sokolate04.xsb", "sokolate05.xsb",
            "sokolate06.xsb", "sokolate07.xsb", "sokolate08.xsb", "sokolate09.xsb", "sokolate10.xsb",
            "sokolate11.xsb", "sokolate12.xsb", "sokolate13.xsb", "sokolate14.xsb", "sokolate15.xsb",
            "sokolate16.xsb", "sokolate17.xsb", "sokolate18.xsb", "sokolate19.xsb", "sokolate20.xsb",
            "sokolate21.xsb", "sokolate22.xsb", "sokolate23.xsb", "sokolate24.xsb", "sokolate25.xsb",
            "sokolate26.xsb", "sokolate27.xsb", "sokolate28.xsb", "sokolate29.xsb", "sokolate30.xsb",
            "sokolate31.xsb", "sokolate32.xsb", "sokolate33.xsb", "sokolate34.xsb", "sokolate35.xsb",
            "sokolate36.xsb", "sokolate37.xsb", "sokolate38.xsb", "sokolate39.xsb", "sokolate40.xsb",
            "sokolate41.xsb", "sokolate42.xsb", "sokolate43.xsb", "sokolate44.xsb",
            "soloban01.xsb", "soloban02.xsb", "soloban03.xsb", "soloban04.xsb", "soloban05.xsb",
            "soloban06.xsb", "soloban07.xsb", "soloban08.xsb"//,
            //"100Boxes-01.xsb", "100Boxes-02.xsb", "100Boxes-03.xsb", "100Boxes-04.xsb", "100Boxes-05.xsb",
            //"100Boxes-06.xsb", "100Boxes-07.xsb", "100Boxes-08.xsb", "100Boxes-09.xsb", "100Boxes-10.xsb"//,
            //"7x7-6-temp.xsb", "church-1.xsb"
            //"SasquatchV-19.xsb", "SasquatchV-27.xsb"
    };

    private DatabaseHelper levelsDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setIcon(R.mipmap.ic_launcher_ascii);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSokoban.undo();
                showCurrentState();
            }
        });

        myDisplay =  (SokobanStateViewNice) findViewById(R.id.my_display);
        //myDisplay =  (SokobanStateViewASCII) findViewById(R.id.my_display);

        if (mSokoban == null) {
            mSokoban = new MySokobanModel(new MySokoban(LEVELS, this));
            mSokoban.addSokobanListener(this);
        }

        updateValuesFromBundle(savedInstanceState);

        levelsDB = new DatabaseHelper(this);

        mSokoban.init();

        /*
        // COMPUTE AND LIST BEST SCORES
        mSokoban.removeSokobanListener(this);
        for (int i =  90; i < mSokoban.getCountLevelsLoaded() ; i++) {
            mSokoban.goToLevel(i);
            SokobanSolution ss = new SokobanSolution(mSokoban.getSokoban(), 100000);
            ss.run();
            DevTools.FORCE_DEBUG(mSokoban.getXSBFile(i).getTitle() + " " + mSokoban.getHistorySize());
        }
        mSokoban.addSokobanListener(this);
        */
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSokoban.goToLevel(savedInstanceState.getInt(STATE_CURRENT_LEVEL));
            zoomMaxNumDisplayedElements = savedInstanceState.getInt(STATE_ZOOM);
        } else {
            mSokoban.goToLevel(0);
            zoomMaxNumDisplayedElements = DEFAULT_MAX_ELEMENTS;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int moveDirection = MyMotionEvent.move(event);

        switch (moveDirection) {
            case MyMotionEvent.NORTH: {
                if (!mSokoban.isWinning()) {
                    mSokoban.move(new Sokoban.Direction(Sokoban.Direction.UP));
                    //updateLevelDatabase();
                }
                return true;
            }
            case MyMotionEvent.SOUTH: {
                if (!mSokoban.isWinning()) {
                    mSokoban.move(new Sokoban.Direction(Sokoban.Direction.DOWN));
                    //updateLevelDatabase();
                }
                return true;
            }
            case MyMotionEvent.EAST: {
                if (!mSokoban.isWinning()) {
                    mSokoban.move(new Sokoban.Direction(Sokoban.Direction.RIGHT));
                    //updateLevelDatabase();
                }
                return true;
            }
            case MyMotionEvent.WEST: {
                if (!mSokoban.isWinning()) {
                    mSokoban.move(new Sokoban.Direction(Sokoban.Direction.LEFT));
                    //updateLevelDatabase();
                }
                return true;
            }

        }
        return false; // event was not handled
    }


    @Override
    public void onBackPressed() {
        // we'll ask confirmation
        new AlertDialog.Builder(this)
                .setTitle(R.string.text_really_quit)
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current state of the activity
        savedInstanceState.putInt(STATE_CURRENT_LEVEL, mSokoban.getCurrentLevel());
        savedInstanceState.putInt(STATE_ZOOM, zoomMaxNumDisplayedElements);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        updateLevelInMenu();
        return true;
    }

    private void updateLevelInMenu() {
        if (mMenu == null) return;
        int displayedCurrentLevel = mSokoban.getCurrentLevel()+1;
        (mMenu.findItem(R.id.action_levels)).
                setTitle(getString(R.string.level)+" "+ displayedCurrentLevel);
        if (mMenu != null) {
            onPrepareOptionsMenu(mMenu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_levels) {
            menuActionSelectLevel();
            return true;
        }
        if (id == R.id.action_solve) {
            menuActionSolve();
            return true;
        }
        if (id == R.id.action_reset) {
            menuActionReset();
            return true;
        }
        if (id == R.id.action_zoom) {
            menuActionZoom();
            return true;
        }
        if (id == R.id.action_unzoom) {
            menuActionUnzoom();
            return true;
        }
        if (id == R.id.action_instructions) {
            menuActionInstructions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void menuActionSelectLevel() {

        String title = getString(R.string.text_select_level);

        GridView gridView = new GridView(this);

        gridView.setAdapter(new LevelSelectionAdapter(this, mSokoban, levelsDB));
        gridView.smoothScrollToPosition(mSokoban.getCurrentLevel());

        gridView.setSelection(mSokoban.getCurrentLevel());
        gridView.setVerticalSpacing(10);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle(title);
        final AlertDialog ad = builder.show();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mSokoban.goToLevel(position);
                ad.dismiss();
            }
        });
    }


    private void menuActionSolve() {

        try {
            new SokobanSolutionAsynchTask(mSokoban, this).execute();
        } catch (OutOfMemoryError e) {
            //e.printStackTrace();
            System.err.print("Out of memory during solving.");
        }
    }

    private void menuActionReset() { mSokoban.reset(); }

    private void menuActionInstructions() {
        Intent myIntent = new Intent(MainActivity.this, Instructions.class);
        MainActivity.this.startActivity(myIntent);
    }


    private void menuActionZoom() {
        if (zoomMaxNumDisplayedElements > 5) {
            zoomMaxNumDisplayedElements--;
            showCurrentState();
        }
    }

    private void menuActionUnzoom() {
        if (zoomMaxNumDisplayedElements < 25) {
            zoomMaxNumDisplayedElements++;
            showCurrentState();
        }
    }

    /*
    private void loadXSBFile(String filename) {
        xsbfile = null;
        try {
            xsbfile = XSBReader.read(filename, this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    public void showCurrentState() {
        myDisplay.showState(mSokoban.getCurrentState(),
                            mSokoban.getCountStep(),
                            mSokoban.getCountPush(),
                            zoomMaxNumDisplayedElements,
                            mSokoban.getXSBFile(mSokoban.getCurrentLevel()).getBestStep());
    }

    private void updateLevelDatabase() {
        if (!mSokoban.isWinning() || hadHelp)
            return;

        int numSteps = mSokoban.getCountStep();
        int currentLevel = mSokoban.getCurrentLevel();

        int bestnumSteps = levelsDB.getNumMoves(currentLevel);

        if (bestnumSteps == DatabaseHelper.NOT_COMPLETED)
            levelsDB.insertData(currentLevel, mSokoban.getXSBFile(currentLevel).getTitle(), numSteps);
        else if (numSteps < bestnumSteps)
            levelsDB.updateData(currentLevel,numSteps);
    }




    @Override
    public void sokobanOnMove(Sokoban.Direction dir) {
        if (mSokoban.isWinning()) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if (fab != null) fab.setEnabled(false);
            mMenu.findItem(R.id.action_solve).setEnabled(false);
            updateLevelDatabase();
        }
        showCurrentState();
    }

    @Override
    public void sokobanOnReset() {
        hadHelp = false;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) fab.setEnabled(true);
        if (mMenu != null)
            mMenu.findItem(R.id.action_solve).setEnabled(true);
        showCurrentState();
    }

    @Override
    public void sokobanOnInit() {
        hadHelp = false;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) fab.setEnabled(true);
        if (mMenu != null)
            mMenu.findItem(R.id.action_solve).setEnabled(true);
        showCurrentState();
    }

    @Override
    public void sokobanOnGoToLevel() {
        updateLevelInMenu();
        hadHelp = false;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) fab.setEnabled(true);
        if (mMenu != null)
            mMenu.findItem(R.id.action_solve).setEnabled(true);
        showCurrentState();
    }

    public void setStateHadHelp(boolean hadHelp) {
        this.hadHelp = hadHelp;
    }

}
