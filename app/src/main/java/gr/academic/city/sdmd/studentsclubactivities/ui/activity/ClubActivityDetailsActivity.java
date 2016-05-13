package gr.academic.city.sdmd.studentsclubactivities.ui.activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import android.support.v7.widget.Toolbar;


import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.ui.activity.fragments.ClubActivityDetailsFragment;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubActivityDetailsActivity extends ToolbarActivity implements ClubActivityDetailsFragment.OnFragmentInteractionListener {

    private static final String EXTRA_CLUB_ACTIVITY_ID = "club_activity_id";
    private static final String EXTRA_ACTIVITY_ID = "Activity_ID";
    private long clubActivityId;


    public static Intent getStartIntent(Context context, long clubActivityId) {
        Intent intent = new Intent(context, ClubActivityDetailsActivity.class);
        intent.putExtra(EXTRA_CLUB_ACTIVITY_ID, clubActivityId);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activity_details);

        clubActivityId = getIntent().getLongExtra(EXTRA_CLUB_ACTIVITY_ID, -1);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_detail_container, ClubActivityDetailsFragment.newInstance(clubActivityId));
        fragmentTransaction.commit();

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


    private void deleteClubActivity(Long activity) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACTIVITY_ID, activity);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onClubActivityDeleted(Long activity) {
        deleteClubActivity(activity);
    }

}
