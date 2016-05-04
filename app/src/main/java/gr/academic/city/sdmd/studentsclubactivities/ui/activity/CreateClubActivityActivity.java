package gr.academic.city.sdmd.studentsclubactivities.ui.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.service.ClubActivityService;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;

/**
 * Created by trumpets on 4/13/16.
 */
public class CreateClubActivityActivity extends ToolbarActivity implements OnMapReadyCallback {


    private static final String EXTRA_CLUB_SERVER_ID = "club_server_id";

    public static Intent getStartIntent(Context context, long clubServerId) {
        Intent intent = new Intent(context, CreateClubActivityActivity.class);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);

        return intent;
    }

    private GoogleMap googleMap;
    private LatLng pointLocation;
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

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


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


    }

    private void saveNewClubActivity() {
        ClubActivityService.startCreateClubActivity(this,
                clubServerId,
                txtTitle.getText().toString(),
                txtShortNote.getText().toString(),
                txtLongNote.getText().toString(),
                timestamp, pointLocation.latitude, pointLocation.longitude, pointLocation.toString());

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
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleResource() {
        return R.string.home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveNewClubActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Drawing marker on the map
                drawMarker(point);
                pointLocation = point;


            }
        });

        }

    private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);
    }

}

