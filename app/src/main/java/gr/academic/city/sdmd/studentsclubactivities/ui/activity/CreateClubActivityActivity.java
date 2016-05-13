package gr.academic.city.sdmd.studentsclubactivities.ui.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.service.ClubActivityService;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;

import static gr.academic.city.sdmd.studentsclubactivities.R.*;

/**
 * Created by trumpets on 4/13/16.
 */
public class CreateClubActivityActivity extends ToolbarActivity implements OnMapReadyCallback {


    private static final String EXTRA_CLUB_SERVER_ID = "club_server_id";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static Intent getStartIntent(Context context, long clubServerId) {
        Intent intent = new Intent(context, CreateClubActivityActivity.class);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);

        return intent;
    }

    private GoogleMap googleMap;
    private LatLng pointLocation;
    private LatLng defaultPointLocation = new LatLng(40.6401,22.9444);
    private long clubServerId;
    private String addressString;

    private EditText txtTitle;
    private EditText txtShortNote;
    private EditText txtLongNote;
    private TextView tvDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CLUB_ACTIVITIES_DATE_FORMAT);

    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_create_club_activity);

        LatLng defaultPointLocation = new LatLng(40.6401,22.9444);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getMapAsync(this);

        Toolbar myChildToolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                addressString = (String) place.getName();
                drawMarker(place.getLatLng());
                Log.i("LOG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("LOG", "An error occurred: " + status);
            }
        });


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


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void saveNewClubActivity() {
        //for test purposes, added as a default place
        if (pointLocation==null){
            pointLocation = defaultPointLocation;
            addressString = getString(string.thessaloniki_city);
        }

        ClubActivityService.startCreateClubActivity(this,
                clubServerId,
                txtTitle.getText().toString(),
                txtShortNote.getText().toString(),
                txtLongNote.getText().toString(),
                timestamp, pointLocation.latitude, pointLocation.longitude, addressString);

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


    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        LatLng cityCollegeLocation = new LatLng(40.637679,22.9350098);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityCollegeLocation, 13));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                                            @Override
                                            public void onMapClick(LatLng point) {

                                                Geocoder gc = new Geocoder(CreateClubActivityActivity.this, Locale.getDefault());

                                                try {

                                                    List<Address> addresses = gc.getFromLocation(point.latitude, point.longitude, 1);
                                                    StringBuilder sb = new StringBuilder();

                                                    if (addresses.size() > 0) {
                                                        Address address = addresses.get(0);

                                                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                                                            sb.append(address.getAddressLine(i)).append("\n");

                                                        sb.append(address.getCountryName());
                                                    }
                                                    addressString = sb.toString();
                                                } catch (IOException e) {
                                                }

                                                if (addressString == null) {

                                                    addressString = "No location found";
                                                }


                                                // Drawing marker on the map
                                                drawMarker(point);

                                            }
                                        }

        );


    }

    private void drawMarker(final LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        markerOptions.title(addressString);

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);

        pointLocation = point;

    }

}

