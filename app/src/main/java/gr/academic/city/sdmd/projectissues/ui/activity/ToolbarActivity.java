package gr.academic.city.sdmd.projectissues.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import gr.academic.city.sdmd.projectissues.R;



/**
 * Created by trumpets on 4/14/16.
 */
public abstract class ToolbarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getTitleResource());
    }

    protected abstract int getContentView();

    protected abstract int getTitleResource();
}
