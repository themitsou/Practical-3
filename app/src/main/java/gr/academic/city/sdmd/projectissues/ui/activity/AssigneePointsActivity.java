package gr.academic.city.sdmd.projectissues.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import gr.academic.city.sdmd.projectissues.R;
import gr.academic.city.sdmd.projectissues.db.AssigneeClientCursorAdapter;
import gr.academic.city.sdmd.projectissues.db.ClientCursorAdapter;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementDBHelper;
import gr.academic.city.sdmd.projectissues.service.ProjectService;
import gr.academic.city.sdmd.projectissues.service.WorkLogService;

public class AssigneePointsActivity extends ToolbarActivity {
    AssigneeClientCursorAdapter customAdapter;
    private Cursor mCursor;
    private ListView listView;
    private long serverId;

    private static final String EXTRA_SERVER_ID = "server_id";


    public static Intent getStartIntent(Context context, long ServerId) {
        Intent intent = new Intent(context, AssigneePointsActivity.class);
        intent.putExtra(EXTRA_SERVER_ID, ServerId);

        return intent;
    }

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignee_points);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.serverId = getIntent().getLongExtra(EXTRA_SERVER_ID, -1);

        listView = (ListView) findViewById(R.id.list);

        ProjectManagementDBHelper dbHelper = new ProjectManagementDBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        String query = "Select a._id, a.assignee_server_id, a.assignee_name,  (a.estimated_hours + (a.estimated_hours- sum(b.work_hours))) as points" +
                " from project_issue a" +
                " join  work_log b on a.server_id = b.issue_server_id" +
                " where a.club_server_id = "+ Long.toString(serverId)+
                " group by a.assignee_server_id"+
                " order by (a.estimated_hours + (a.estimated_hours- sum(b.work_hours))) desc";

        mCursor = sqLiteDatabase.rawQuery(query, null);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                customAdapter = new AssigneeClientCursorAdapter(
                        AssigneePointsActivity.this,
                        mCursor,
                        0);

                listView.setAdapter(customAdapter);
            }

        });


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

}


