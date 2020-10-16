package eu.yalacirodev.sokoban;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class Instructions extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarinstructions);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.text_instructions));
            //getSupportActionBar().setIcon(R.mipmap.ic_launcher_ascii);
        }
        writeInstructions();
    }


    private void writeInstructions() {
        TextView myDisplay = (TextView) findViewById(R.id.my_display_instructions);
        if(myDisplay != null) {
            myDisplay.setMovementMethod(new ScrollingMovementMethod());
            //myDisplay.append(getString(R.string.text_instructions));
            //myDisplay.append(getString(R.string.text_game_rules));
            myDisplay.setText(R.string.text_game_rules);
        }
    }

}
