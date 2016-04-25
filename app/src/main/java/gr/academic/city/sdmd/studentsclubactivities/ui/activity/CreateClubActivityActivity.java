package gr.academic.city.sdmd.studentsclubactivities.ui.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.service.ClubActivityService;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;

/**
 * Created by trumpets on 4/13/16.
 */
public class CreateClubActivityActivity extends AppCompatActivity {

    private static final String EXTRA_CLUB_SERVER_ID = "club_server_id";

    public static Intent getStartIntent(Context context, long clubServerId) {
        Intent intent = new Intent(context, CreateClubActivityActivity.class);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);

        return intent;
    }

    private long clubServerId;

    private EditText txtTitle;
    private EditText txtShortNote;
    private EditText txtLongNote;
    private TextView tvDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CLUB_ACTIVITIES_DATE_FORMAT);

    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club_activity);

        clubServerId = getIntent().getLongExtra(EXTRA_CLUB_SERVER_ID, -1);

        txtTitle = (EditText) findViewById(R.id.txt_club_activity_title);
        txtShortNote = (EditText) findViewById(R.id.txt_club_activity_short_note);
        txtLongNote = (EditText) findViewById(R.id.txt_club_activity_long_note);
        tvDate = (TextView) findViewById(R.id.tv_club_activity_date);

        Date now = new Date();
        setupDate(now);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewClubActivity();
            }
        });
        
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveNewClubActivity() {
        ClubActivityService.startCreateClubActivity(this,
                clubServerId,
                txtTitle.getText().toString(),
                txtShortNote.getText().toString(),
                txtLongNote.getText().toString(),
                timestamp);

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
}
