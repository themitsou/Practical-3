package gr.academic.city.sdmd.studentsclubactivities.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.MessageFormat;

import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.domain.ClubActivity;
import gr.academic.city.sdmd.studentsclubactivities.receiver.TriggerPushToServerBroadcastReceiver;
import gr.academic.city.sdmd.studentsclubactivities.util.Commons;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;
import gr.academic.city.sdmd.studentsclubactivities.util.GsonResponseCallback;

import static gr.academic.city.sdmd.studentsclubactivities.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubActivityService extends IntentService {

    private static final String ACTION_FETCH_CLUB_ACTIVITIES = "gr.academic.city.sdmd.studentsclubactivities.FETCH_ACTIVITIES";
    private static final String EXTRA_CLUB_SERVER_ID = "club_server_id";

    private static final String ACTION_CREATE_CLUB_ACTIVITY = "gr.academic.city.sdmd.studentsclubactivities.CREATE_CLUB_ACTIVITY";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_SHORT_NOTE = "short_note";
    private static final String EXTRA_LONG_NOTE = "long_note";
    private static final String EXTRA_TIMESTAMP = "timestamp";
    private static final String EXTRA_LATITUDE = "latitude";
    private static final String EXTRA_LONGITUDE = "longitude";
    private static final String EXTRA_LOCATION = "location";

    private static final String ACTION_DELETE_CLUB_ACTIVITY = "gr.academic.city.sdmd.studentsclubactivities.DELETE_CLUB_ACTIVITY";
    private static final String ACTION_UNDELETE_CLUB_ACTIVITY = "gr.academic.city.sdmd.studentsclubactivities.UNDELETE_CLUB_ACTIVITY";
    private static final String EXTRA_CLUB_SERVER_ACTIVITY_ID = "club_server_activity_id";

    public static void startFetchActivities(Context context,  long clubServerId) {
        Intent intent = new Intent(context, ClubActivityService.class);
        intent.setAction(ACTION_FETCH_CLUB_ACTIVITIES);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);

        context.startService(intent);
    }

    public static void startDeleteActivity(Context context,  long clubServerActivityId) {
        Intent intent = new Intent(context, ClubActivityService.class);
        intent.setAction(ACTION_DELETE_CLUB_ACTIVITY);
        intent.putExtra(EXTRA_CLUB_SERVER_ACTIVITY_ID, clubServerActivityId);

        context.startService(intent);
    }

    public static void startUnDeleteActivity(Context context,  long clubServerActivityId) {
        Intent intent = new Intent(context, ClubActivityService.class);
        intent.setAction(ACTION_UNDELETE_CLUB_ACTIVITY);
        intent.putExtra(EXTRA_CLUB_SERVER_ACTIVITY_ID, clubServerActivityId);

        context.startService(intent);
    }

    public static void startCreateClubActivity(Context context, long clubServerId, String title, String shortNote, String longNote, long timestamp, double latitude, double longitude, String location) {
        Intent intent = new Intent(context, ClubActivityService.class);
        intent.setAction(ACTION_CREATE_CLUB_ACTIVITY);
        intent.putExtra(EXTRA_CLUB_SERVER_ID, clubServerId);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_SHORT_NOTE, shortNote);
        intent.putExtra(EXTRA_LONG_NOTE, longNote);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        intent.putExtra(EXTRA_LOCATION, location);

        context.startService(intent);
    }

    public ClubActivityService() {
        super("ClubActivityService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_FETCH_CLUB_ACTIVITIES.equals(intent.getAction())) {
            fetchActivities(intent);
        } else if (ACTION_CREATE_CLUB_ACTIVITY.equals(intent.getAction())) {
            createClubActivity(intent);
        }else if(ACTION_DELETE_CLUB_ACTIVITY.equals(intent.getAction())){
            deleteClubActivity(intent);
        }else if(ACTION_UNDELETE_CLUB_ACTIVITY.equals(intent.getAction())){
            unDeleteClubActivity(intent);
        } else {
            throw new UnsupportedOperationException("Action not supported: " + intent.getAction());
        }
    }

    private void fetchActivities(Intent intent) {
        long clubServerId = intent.getLongExtra(EXTRA_CLUB_SERVER_ID, -1);
        executeRequest(MessageFormat.format(Constants.CLUB_ACTIVITIES_URL, clubServerId), Commons.ConnectionMethod.GET, null, new GsonResponseCallback<ClubActivity[]>(ClubActivity[].class) {
            @Override
            protected void onResponse(int responseCode, ClubActivity[] clubActivities) {

                for (ClubActivity clubActivity : clubActivities) {
                    if (getContentResolver().query(
                            ClubManagementContract.ClubActivity.CONTENT_URI,
                            new String[0],
                            ClubManagementContract.ClubActivity.COLUMN_NAME_SERVER_ID + " = ?",
                            new String[]{String.valueOf(clubActivity.getServerId())},
                            null).getCount() == 0) {

                        // this club activity is not in db, add it
                        ContentValues contentValues = clubActivity.toContentValues();
                        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_UPLOADED_TO_SERVER, 1);

                        getContentResolver().insert(
                                ClubManagementContract.ClubActivity.CONTENT_URI,
                                contentValues);
                    }
                }
            }
        });
    }

    private void createClubActivity(Intent intent) {
        long clubServerId = intent.getLongExtra(EXTRA_CLUB_SERVER_ID, -1);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String shortNote = intent.getStringExtra(EXTRA_SHORT_NOTE);
        String longNote = intent.getStringExtra(EXTRA_LONG_NOTE);
        long timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, -1);
        double latitude =  intent.getDoubleExtra(EXTRA_LATITUDE, -1);
        double longitude =  intent.getDoubleExtra(EXTRA_LONGITUDE, -1);
        String location = intent.getStringExtra(EXTRA_LOCATION);

        ContentValues contentValues = new ClubActivity(title, shortNote, longNote, timestamp, latitude, longitude, location, clubServerId).toContentValues();
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_UPLOADED_TO_SERVER, 0);
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_SERVER_ID, -1);
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_FOR_DELETION, 0);

        getContentResolver().insert(
                ClubManagementContract.ClubActivity.CONTENT_URI,
                contentValues
                );

        sendBroadcast(new Intent(TriggerPushToServerBroadcastReceiver.ACTION_TRIGGER));
    }

    private void deleteClubActivity (Intent intent) {
        long clubServerActivityId = intent.getLongExtra(EXTRA_CLUB_SERVER_ACTIVITY_ID, -1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_FOR_DELETION, 1);

        getContentResolver().update(
                ContentUris.withAppendedId(ClubManagementContract.ClubActivity.CONTENT_URI, clubServerActivityId),
                contentValues,
                null,
                null
        );


    }
    private void unDeleteClubActivity (Intent intent) {
        long clubServerActivityId = intent.getLongExtra(EXTRA_CLUB_SERVER_ACTIVITY_ID, -1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_FOR_DELETION, 0);

        getContentResolver().update(
                ContentUris.withAppendedId(ClubManagementContract.ClubActivity.CONTENT_URI, clubServerActivityId),
                contentValues,
                null,
                null
        );


    }
}
