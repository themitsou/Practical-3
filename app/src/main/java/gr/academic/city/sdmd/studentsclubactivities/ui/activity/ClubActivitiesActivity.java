package gr.academic.city.sdmd.studentsclubactivities.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.receiver.TriggerPushToServerBroadcastReceiver;
import gr.academic.city.sdmd.studentsclubactivities.service.ClubActivityService;
import gr.academic.city.sdmd.studentsclubactivities.ui.activity.fragments.BlankFragment;
import gr.academic.city.sdmd.studentsclubactivities.ui.activity.fragments.ClubActivitiesFragment;
import gr.academic.city.sdmd.studentsclubactivities.ui.activity.fragments.ClubActivityDetailsFragment;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubActivitiesActivity extends ToolbarActivity implements ClubActivitiesFragment.OnListFragmentInteractionListener, ClubActivityDetailsFragment.OnFragmentInteractionListener {
    private static final int DELETE_REQUEST = 1;
    private static final String EXTRA_CLUB_SERVER_ID = "club_server_id";

    private static final String EXTRA_ACTIVITY_ID = "Activity_ID";

    public static Intent getStartIntent(Context context, long clubServerId) {
        Intent intent = new Intent(context, ClubActivitiesActivity.class);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);

        return intent;
    }

    private long clubServerId;
    private boolean isDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activities);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.clubServerId = getIntent().getLongExtra(EXTRA_CLUB_SERVER_ID, -1);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        ClubActivitiesFragment fragment = ClubActivitiesFragment.newInstance(clubServerId);
        fragmentTransaction.add(R.id.fragment_list_container, fragment);
        fragmentTransaction.commit();

        View fragmentContainer = findViewById(R.id.fragment_detail_container);
        isDualPane = fragmentContainer != null &&
                fragmentContainer.getVisibility() == View.VISIBLE;
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DELETE_REQUEST) {
            if (resultCode == RESULT_OK) {
                final Long deleteActivityID = data.getLongExtra(EXTRA_ACTIVITY_ID, -1);
                ClubActivityService.startDeleteActivity(ClubActivitiesActivity.this, deleteActivityID);

                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.msg_snackbar, Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        switch (event) {
                            case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                // do nothing
                                break;
                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                sendBroadcast(new Intent(TriggerPushToServerBroadcastReceiver.ACTION_TRIGGER));
                                break;
                        }
                    }
                }).setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClubActivityService.startUnDeleteActivity(ClubActivitiesActivity.this, deleteActivityID);
                    }
                }).show();


            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ClubActivityService.startFetchActivities(this, clubServerId);
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
        getMenuInflater().inflate(R.menu.activities_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(CreateClubActivityActivity.getStartIntent(ClubActivitiesActivity.this, clubServerId));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClubActivitySelected(Long activity) {
        if (isDualPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_detail_container, ClubActivityDetailsFragment.newInstance(activity));
            fragmentTransaction.commit();
        } else {
            startActivityForResult(ClubActivityDetailsActivity.getStartIntent(ClubActivitiesActivity.this, activity), DELETE_REQUEST);
        }

    }

    @Override
    public void onClubActivityDeleted(final Long activity) {
        ClubActivityService.startDeleteActivity(ClubActivitiesActivity.this, activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_detail_container,new BlankFragment());
        fragmentTransaction.commit();
        Snackbar.make(findViewById(R.id.coordinator_layout), R.string.msg_snackbar, Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        // do nothing
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        sendBroadcast(new Intent(TriggerPushToServerBroadcastReceiver.ACTION_TRIGGER));
                        break;
                    default:
                        sendBroadcast(new Intent(TriggerPushToServerBroadcastReceiver.ACTION_TRIGGER));
                        break;
                }
            }
        }).setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClubActivityService.startUnDeleteActivity(ClubActivitiesActivity.this, activity);
            }
        }).show();
    }
}
