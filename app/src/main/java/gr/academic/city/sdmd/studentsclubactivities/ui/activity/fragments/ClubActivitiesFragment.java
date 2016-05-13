package gr.academic.city.sdmd.studentsclubactivities.ui.activity.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.service.ClubActivityService;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;


public class ClubActivitiesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnListFragmentInteractionListener {
        void onClubActivitySelected(Long activity);
    }

    private static final String ARG_PARAM1 = "param1";
    private Long mParam1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CursorAdapter adapter;
    private View view;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CLUB_ACTIVITIES_DATE_FORMAT);
    private static final int CLUB_ACTIVITIES_LOADER = 10;
    private long clubServerId;
    private WeakReference<FetchClubActivitiesAsyncTast> asyncTaskWeakRef;

    private static final String[] PROJECTION = {
            ClubManagementContract.ClubActivity._ID,
            ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP,
            ClubManagementContract.ClubActivity.COLUMN_NAME_SERVER_ID
    };

    private static final String SORT_ORDER = ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP + " DESC";


    private final static String[] FROM_COLUMNS = {
            ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE,
            ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP};

    private final static int[] TO_IDS = {
            R.id.tv_club_activity_title,
            R.id.tv_club_activity_short_note,
            R.id.tv_club_activity_date};



    private OnListFragmentInteractionListener mListener;


    public static ClubActivitiesFragment newInstance(Long param1) {
        ClubActivitiesFragment fragment = new ClubActivitiesFragment(param1);
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    public ClubActivitiesFragment(long clubServerId) {
        // Required empty public constructor
    }

    public ClubActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
        }
        clubServerId = mParam1;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
       view = inflater.inflate(R.layout.fragment_club_activities, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateClubActivitiesRefresh();
            }
        });

        adapter = new SimpleCursorAdapter(view.getContext(), R.layout.item_club_activity, null, FROM_COLUMNS, TO_IDS, 0);
        ((SimpleCursorAdapter) adapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP) && view instanceof TextView) {
                    // we have to convert the timestamp to a human readable date)

                    TextView textView = (TextView) view;
                    textView.setText(dateFormat.format(new Date(cursor.getLong(columnIndex))));
                    return true;
                } else {
                    return false;
                }
            }
        });

        ListView resultsListView = (ListView) view.findViewById(android.R.id.list);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onClubActivitySelected(id);
            }
        });


        getLoaderManager().initLoader(CLUB_ACTIVITIES_LOADER, null, this);

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
    public void onRefresh() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLUB_ACTIVITIES_LOADER:
                return new CursorLoader(view.getContext(),
                        ClubManagementContract.ClubActivity.CONTENT_URI,
                        PROJECTION,
                        ClubManagementContract.ClubActivity.COLUMN_NAME_CLUB_SERVER_ID + " = ? AND "+ ClubManagementContract.ClubActivity.COLUMN_NAME_FOR_DELETION + " = ? "  ,
                        new String[]{String.valueOf(clubServerId),String.valueOf(0)},
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    private void initiateClubActivitiesRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        FetchClubActivitiesAsyncTast asyncTask = new FetchClubActivitiesAsyncTast(this);
        this.asyncTaskWeakRef = new WeakReference<FetchClubActivitiesAsyncTast>(asyncTask );
        asyncTask.execute(clubServerId);
    }

    private class FetchClubActivitiesAsyncTast extends AsyncTask<Long, Void, Void> {

        private WeakReference<ClubActivitiesFragment> fragmentWeakRef;

        private FetchClubActivitiesAsyncTast (ClubActivitiesFragment fragment) {
            this.fragmentWeakRef = new WeakReference<ClubActivitiesFragment>(fragment);
        }

        @Override
        protected Void doInBackground(Long... params) {
            View my_view = view;
            ClubActivityService.startFetchActivities(this.fragmentWeakRef.get().getContext(), params[0]);

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

}
