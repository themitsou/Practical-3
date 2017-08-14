package gr.academic.city.sdmd.projectissues.ui.activity;

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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import gr.academic.city.sdmd.projectissues.R;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.service.ProjectService;
import gr.academic.city.sdmd.projectissues.service.WorkLogService;

public class MainActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = {
            ProjectManagementContract.Project._ID,
            ProjectManagementContract.Project.COLUMN_NAME_PROJECT_NAME,
            ProjectManagementContract.Project.COLUMN_NAME_SERVER_ID
    };

    private static final String SORT_ORDER = ProjectManagementContract.Project.COLUMN_NAME_PROJECT_NAME + " ASC";

    private static final int CLUBS_LOADER = 0;

    private final static String[] FROM_COLUMNS = {
            ProjectManagementContract.Project.COLUMN_NAME_PROJECT_NAME};

    private final static int[] TO_IDS = {
            R.id.tv_club_name};

    private CursorAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        adapter = new SimpleCursorAdapter(this, R.layout.item_club, null, FROM_COLUMNS, TO_IDS, 0);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateClubsRefresh();
            }
        });

        ListView resultsListView = (ListView) findViewById(R.id.list);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                if (cursor.moveToPosition(position)) {
                    startActivity(ClubActivitiesActivity.getStartIntent(MainActivity.this,
                            cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.Project.COLUMN_NAME_SERVER_ID))));
                }
            }
        });

        getSupportLoaderManager().initLoader(CLUBS_LOADER, null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        ProjectService.startFetchProjects(this);
        WorkLogService.startFetchWorkLogs(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLUBS_LOADER:
                return new CursorLoader(this,
                        ProjectManagementContract.Project.CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
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

    private void initiateClubsRefresh() {
        new FetchClubsAsyncTask().execute();
    }

    private class FetchClubsAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            ProjectService.startFetchProjects(MainActivity.this);

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
        getMenuInflater().inflate(R.menu.points_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
                case R.id.action_points:
                intent = new Intent(this, PointsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_assignee_points:
                intent = new Intent(this, AssigneePointsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
