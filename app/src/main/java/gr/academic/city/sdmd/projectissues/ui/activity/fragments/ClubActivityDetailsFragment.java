package gr.academic.city.sdmd.projectissues.ui.activity.fragments;

import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;

import gr.academic.city.sdmd.projectissues.R;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.util.Constants;

public class ClubActivityDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnFragmentInteractionListener {
        void onClubActivityDeleted(Long activity);
    }

    private static final String ARG_PARAM1 = "param1";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PROJECT_ISSUES_DATE_FORMAT);
    private static final int CLUB_ACTIVITY_LOADER = 20;
    private static final String EXTRA_ACTIVITY_ID = "Activity_ID";

    private long clubActivityId;

    private TextView tvTitle;
    private TextView tvShortNote;
    private TextView tvLongNote;
    private TextView tvDate;

    private View view;
    private Long mParam1;

    private OnFragmentInteractionListener mListener;

    public ClubActivityDetailsFragment() {
        // Required empty public constructor
    }


    public static ClubActivityDetailsFragment newInstance(Long param1) {
        ClubActivityDetailsFragment fragment = new ClubActivityDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
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
        view = inflater.inflate(R.layout.fragment_club_activity_details, container, false);

        tvTitle = (TextView) view.findViewById(R.id.tv_club_activity_title);
        tvShortNote = (TextView) view.findViewById(R.id.tv_club_activity_short_note);
        tvLongNote = (TextView) view.findViewById(R.id.tv_club_activity_long_note);
        tvDate = (TextView) view.findViewById(R.id.tv_club_activity_date);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        final ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 1000, 0); // see this max value coming back here, we animate towards that value


        final FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (animation.isRunning()) {
                    animation.end();
                    myFab.setImageResource(android.R.drawable.ic_media_play);
                }else{
                    animation.setDuration(1500000); //in milliseconds
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                    myFab.setImageResource(android.R.drawable.checkbox_off_background);
                }

            }

        });

        getLoaderManager().initLoader(CLUB_ACTIVITY_LOADER, null, this);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLUB_ACTIVITY_LOADER:
                return new CursorLoader(view.getContext(),
                        ContentUris.withAppendedId(ProjectManagementContract.ProjectIssue.CONTENT_URI, clubActivityId),
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
            tvTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_TITLE)));
            tvShortNote.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SHORT_NOTE)));
            tvLongNote.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_LONG_NOTE)));
            tvDate.setText(dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_TIMESTAMP)))));


        }

        if (cursor != null) {
            cursor.close();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mListener.onClubActivityDeleted(clubActivityId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
