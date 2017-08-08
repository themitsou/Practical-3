package gr.academic.city.sdmd.projectissues.ui.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import gr.academic.city.sdmd.projectissues.R;
import gr.academic.city.sdmd.projectissues.service.ProjectIssueService;
import gr.academic.city.sdmd.projectissues.util.Constants;

import static gr.academic.city.sdmd.projectissues.R.*;

/**
 * Created by trumpets on 4/13/16.
 */
public class CreateClubActivityActivity extends ToolbarActivity {


    private static final String EXTRA_CLUB_SERVER_ID = "club_server_id";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    public static Intent getStartIntent(Context context, long clubServerId) {
        Intent intent = new Intent(context, CreateClubActivityActivity.class);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);

        return intent;
    }


    private long clubServerId;
    private String addressString;

    private EditText txtTitle;
    private EditText txtShortNote;
    private EditText txtLongNote;
    private TextView tvDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PROJECT_ISSUES_DATE_FORMAT);

    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_create_club_activity);



        Toolbar myChildToolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        clubServerId = getIntent().getLongExtra(EXTRA_CLUB_SERVER_ID, -1);

        txtTitle = (EditText) findViewById(id.txt_club_activity_title);
        txtShortNote = (EditText) findViewById(id.txt_club_activity_short_note);
        txtLongNote = (EditText) findViewById(id.txt_club_activity_long_note);
        tvDate = (TextView) findViewById(id.tv_club_activity_date);

        Date now = new Date();
        setupDate(now);


        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });




    }

    private void saveNewClubActivity() {
        //for test purposes, added as a default place


        ProjectIssueService.startCreateProjectIssue(this,
                clubServerId,
                txtTitle.getText().toString(),
                txtShortNote.getText().toString(),
                txtLongNote.getText().toString(),
                timestamp, 0,0, addressString);

        finish();
    }

    private void setupDate(Date date) {
        timestamp = date.getTime();
        tvDate.setText(dateFormat.format(date));
    }

    private void showDatePicker() {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear); // really important to know is that java's calendar has 0-based months (0 = January, 5 = June etc)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                setupDate(c.getTime());
            }
        }, year, month, day);

        dialog.show();
    }

    @Override
    protected int getContentView() {
        return layout.activity_main;
    }

    @Override
    protected int getTitleResource() {
        return string.home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.action_save:
                saveNewClubActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}

