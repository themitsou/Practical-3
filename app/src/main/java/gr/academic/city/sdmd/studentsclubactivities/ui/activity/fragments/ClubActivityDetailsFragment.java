package gr.academic.city.sdmd.studentsclubactivities.ui.activity.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ClubActivityDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClubActivityDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> , OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CLUB_ACTIVITIES_DATE_FORMAT);
    private static final int CLUB_ACTIVITY_LOADER = 20;
    private static final String EXTRA_ACTIVITY_ID = "Activity_ID";

    private long clubActivityId;

    private TextView tvTitle;
    private TextView tvShortNote;
    private TextView tvLongNote;
    private TextView tvDate;

    private GoogleMap googleMap;

    private Double latitude;
    private Double longitude;
    private String location;
    private SupportMapFragment fragment;


    private View view;

    // TODO: Rename and change types of parameters
    private Long mParam1;

     public ClubActivityDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment ClubActivityDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClubActivityDetailsFragment newInstance(Long param1) {
        ClubActivityDetailsFragment fragment = new ClubActivityDetailsFragment(param1);
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ClubActivityDetailsFragment(long clubActivityId) {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
        }
        clubActivityId = mParam1;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_club_activity_details, container, false);

        MapFragment mapFragment;
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        fragment.getMapAsync(this);



        tvTitle = (TextView) view.findViewById(R.id.tv_club_activity_title);
        tvShortNote = (TextView) view.findViewById(R.id.tv_club_activity_short_note);
        tvLongNote = (TextView) view.findViewById(R.id.tv_club_activity_long_note);
        tvDate = (TextView) view.findViewById(R.id.tv_club_activity_date);

        getLoaderManager().initLoader(CLUB_ACTIVITY_LOADER, null, this);

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLUB_ACTIVITY_LOADER:
                return new CursorLoader(view.getContext(),
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

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


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
        intent.putExtra(EXTRA_ACTIVITY_ID, clubActivityId);
//        setResult(RESULT_OK, intent);
//        finish();
    }

}
