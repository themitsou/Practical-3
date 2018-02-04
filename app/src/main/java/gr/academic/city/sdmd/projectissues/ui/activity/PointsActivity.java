package gr.academic.city.sdmd.projectissues.ui.activity;

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
import gr.academic.city.sdmd.projectissues.db.ClientCursorAdapter;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementDBHelper;
import gr.academic.city.sdmd.projectissues.service.ProjectService;
import gr.academic.city.sdmd.projectissues.service.WorkLogService;

public class PointsActivity extends ToolbarActivity {
    ClientCursorAdapter customAdapter;
    private Cursor mCursor;
    private ListView listView;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.list);

        ProjectManagementDBHelper dbHelper = new ProjectManagementDBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();


        String query ="Select c._id, c.server_id, c.project_name,  " +
                " ifNull(2*(select sum(a.estimated_hours) from  project_issue a where  a.club_server_id = c.server_id )-" +
                " (select sum(b.work_hours) from  work_log b where  b.issue_server_id in" +
                " (select d.server_id from  project_issue d where  d.club_server_id = c.server_id )),0) as points" +
                " from  project c " +
                " group by c.server_id, c.project_name";

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
                customAdapter = new ClientCursorAdapter(
                        PointsActivity.this,
                        mCursor,
                        0);

                listView.setAdapter(customAdapter);
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = customAdapter.getCursor();
                if (cursor.moveToPosition(position)) {
                   startActivity(AssigneePointsActivity.getStartIntent(PointsActivity.this,
                            cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.Project.COLUMN_NAME_SERVER_ID))));
                }
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


