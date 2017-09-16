package gr.academic.city.sdmd.projectissues.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import gr.academic.city.sdmd.projectissues.R;
import gr.academic.city.sdmd.projectissues.db.AssigneeClientCursorAdapter;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementDBHelper;

public class AssigneeTrophiesActivity extends ToolbarActivity {


    private int greatPomodori1 = 0;
    private int greatPomodori2 = 0;
    private int greatPomodori3 = 0;
    private int greatPomodori4 = 0;
    private int greatPomodori5 = 0;
    private int greatPomodori6 = 0;
    private int greatPomodori7 = 0;
    private int greatPomodori8 = 0;
    private int greatPomodori9 = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignee_trophies);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        loadData();

        ImageView trophy1 = (ImageView) findViewById(R.id.imageTrophy1);
        ImageView trophy2= (ImageView) findViewById(R.id.imageTrophy2);
        ImageView trophy3 = (ImageView) findViewById(R.id.imageTrophy3);
        ImageView trophy4 = (ImageView) findViewById(R.id.imageTrophy4);
        ImageView trophy5 = (ImageView) findViewById(R.id.imageTrophy5);
        ImageView trophy6 = (ImageView) findViewById(R.id.imageTrophy6);
        ImageView trophy7 = (ImageView) findViewById(R.id.imageTrophy7);
        ImageView trophy8 = (ImageView) findViewById(R.id.imageTrophy8);
        ImageView trophy9 = (ImageView) findViewById(R.id.imageTrophy9);


        if (greatPomodori1==1){
            trophy1.setVisibility(View.VISIBLE);
        }

        if (greatPomodori2==1){
            trophy2.setVisibility(View.VISIBLE);
        }

        if (greatPomodori3==1){
            trophy3.setVisibility(View.VISIBLE);
        }

        if (greatPomodori4==1){
            trophy4.setVisibility(View.VISIBLE);
        }

        if (greatPomodori5==1){
            trophy5.setVisibility(View.VISIBLE);
        }

        if (greatPomodori6==1){
            trophy6.setVisibility(View.VISIBLE);
        }

        if (greatPomodori7==1){
            trophy7.setVisibility(View.VISIBLE);
        }

        if (greatPomodori8==1){
            trophy8.setVisibility(View.VISIBLE);
        }

        if (greatPomodori9==1){
            trophy9.setVisibility(View.VISIBLE);
        }



    }


    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleResource() {
        return R.string.home;
    }

    private void loadData(){
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        greatPomodori1 = sp.getInt("GreatPomodori1", 0);
        greatPomodori2 = sp.getInt("GreatPomodori2", 0);
        greatPomodori3 = sp.getInt("GreatPomodori3", 0);
        greatPomodori4 = sp.getInt("GreatPomodori4", 0);
        greatPomodori5 = sp.getInt("GreatPomodori5", 0);
        greatPomodori6 = sp.getInt("GreatPomodori6", 0);
        greatPomodori7 = sp.getInt("GreatPomodori7", 0);
        greatPomodori8 = sp.getInt("GreatPomodori8", 0);
        greatPomodori9 = sp.getInt("GreatPomodori9", 0);
    }

}


