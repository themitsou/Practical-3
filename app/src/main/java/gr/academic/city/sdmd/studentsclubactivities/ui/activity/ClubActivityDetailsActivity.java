package gr.academic.city.sdmd.studentsclubactivities.ui.activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import gr.academic.city.sdmd.studentsclubactivities.R;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubActivityDetailsActivity extends ToolbarActivity {

    private static final String EXTRA_CLUB_ACTIVITY_ID = "club_activity_id";
    private static final String EXTRA_ACTIVITY_ID = "Activity_ID";


    public static Intent getStartIntent(Context context, long clubActivityId) {
        Intent intent = new Intent(context, ClubActivityDetailsActivity.class);
        intent.putExtra(EXTRA_CLUB_ACTIVITY_ID, clubActivityId);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activity_details);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleResource() {
        return R.string.home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteClubActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteClubActivity() {
        Intent intent = new Intent();
//        intent.putExtra(EXTRA_ACTIVITY_ID, clubActivityId);
        setResult(RESULT_OK, intent);
        finish();
    }



}
