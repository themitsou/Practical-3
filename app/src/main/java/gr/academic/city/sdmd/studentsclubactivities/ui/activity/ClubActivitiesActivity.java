package gr.academic.city.sdmd.studentsclubactivities.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.service.ClubActivityService;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubActivitiesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_CLUB_SERVER_ID = "club_server_id";

    private static final String[] PROJECTION = {
            ClubManagementContract.ClubActivity._ID,
            ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP
    };

    private static final String SORT_ORDER = ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP + " DESC";

    private static final int CLUB_ACTIVITIES_LOADER = 10;

    private final static String[] FROM_COLUMNS = {
            ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP};

    private final static int[] TO_IDS = {
            R.id.tv_club_activity_title,
            R.id.tv_club_activity_short_note,
            R.id.tv_club_activity_date};

    public static Intent getStartIntent(Context context, long clubServerId) {
        Intent intent = new Intent(context, ClubActivitiesActivity.class);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);

        return intent;
    }

    private long clubServerId;
    private CursorAdapter adapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CLUB_ACTIVITIES_DATE_FORMAT);

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activities);

        this.clubServerId = getIntent().getLongExtra(EXTRA_CLUB_SERVER_ID, -1);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateClubActivitiesRefresh();
            }
        });

        adapter = new SimpleCursorAdapter(this, R.layout.item_club_activity, null, FROM_COLUMNS, TO_IDS, 0);
        ((SimpleCursorAdapter) adapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP) && view instanceof TextView) {
                    // we have to convert the timestamp to a human readable date)

                    TextView textView = (TextView) view;
                    textView.setText(dateFormat.format(new Date(cursor.getLong(columnIndex))));
                    return true;
                } else {
                    return false;
                }
            }
        });

        ListView resultsListView = (ListView) findViewById(android.R.id.list);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(ClubActivityDetailsActivity.getStartIntent(ClubActivitiesActivity.this, id));
            }
        });

        findViewById(R.id.btn_add_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CreateClubActivityActivity.getStartIntent(ClubActivitiesActivity.this, clubServerId));
            }
        });

        getSupportLoaderManager().initLoader(CLUB_ACTIVITIES_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ClubActivityService.startFetchActivities(this, clubServerId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLUB_ACTIVITIES_LOADER:
                return new CursorLoader(this,
                        ClubManagementContract.ClubActivity.CONTENT_URI,
                        PROJECTION,
                        ClubManagementContract.ClubActivity.COLUMN_NAME_CLUB_SERVER_ID + " = ?",
                        new String[]{String.valueOf(clubServerId)},
                        SORT_ORDER
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    private void initiateClubActivitiesRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        new FetchClubActivitiesAsyncTast().execute(clubServerId);
    }

    private class FetchClubActivitiesAsyncTast extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            ClubActivityService.startFetchActivities(ClubActivitiesActivity.this, params[0]);

            try {
                // giving the service ample time to finish
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
