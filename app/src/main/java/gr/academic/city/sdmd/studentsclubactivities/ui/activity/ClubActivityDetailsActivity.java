package gr.academic.city.sdmd.studentsclubactivities.ui.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.service.ClubActivityService;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubActivityDetailsActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<Cursor> , OnMapReadyCallback {

    private static final String EXTRA_CLUB_ACTIVITY_ID = "club_activity_id";

    private static final int CLUB_ACTIVITY_LOADER = 20;
    private static final String EXTRA_ACTIVITY_ID = "Activity_ID";

    public static Intent getStartIntent(Context context, long clubActivityId) {
        Intent intent = new Intent(context, ClubActivityDetailsActivity.class);
        intent.putExtra(EXTRA_CLUB_ACTIVITY_ID, clubActivityId);

        return intent;
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CLUB_ACTIVITIES_DATE_FORMAT);

    private long clubActivityId;

    private TextView tvTitle;
    private TextView tvShortNote;
    private TextView tvLongNote;
    private TextView tvDate;

    private GoogleMap googleMap;

    private Double latitude;
    private Double longitude;
    private String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activity_details);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clubActivityId = getIntent().getLongExtra(EXTRA_CLUB_ACTIVITY_ID, -1);

        tvTitle = (TextView) findViewById(R.id.tv_club_activity_title);
        tvShortNote = (TextView) findViewById(R.id.tv_club_activity_short_note);
        tvLongNote = (TextView) findViewById(R.id.tv_club_activity_long_note);
        tvDate = (TextView) findViewById(R.id.tv_club_activity_date);

        getSupportLoaderManager().initLoader(CLUB_ACTIVITY_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLUB_ACTIVITY_LOADER:
                return new CursorLoader(this,
                        ContentUris.withAppendedId(ClubManagementContract.ClubActivity.CONTENT_URI, clubActivityId),
                        null, // NULL because we want every column
                        null,
                        null,
                        null
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateView(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateView(null);
    }

    private void updateView(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            tvTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE)));
            tvShortNote.setText(cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE)));
            tvLongNote.setText(cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LONG_NOTE)));
            tvDate.setText(dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP)))));
            latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LAT));
            longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LNG));
            location = cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LOCATION));
            LatLng pointLocation = new LatLng(latitude,longitude);
            drawMarker(pointLocation);
        }

        if (cursor != null) {
            cursor.close();
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
        intent.putExtra(EXTRA_ACTIVITY_ID,clubActivityId);
        setResult(RESULT_OK, intent);
//        ClubActivityService.startDeleteActivity(this,clubActivityId);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

    }

    private void drawMarker(final LatLng point) {

        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        markerOptions.title(location);

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

    }
}
