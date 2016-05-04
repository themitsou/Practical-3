package gr.academic.city.sdmd.studentsclubactivities.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.text.MessageFormat;

import gr.academic.city.sdmd.studentsclubactivities.R;
import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.domain.ClubActivity;
import gr.academic.city.sdmd.studentsclubactivities.ui.activity.ClubActivitiesActivity;
import gr.academic.city.sdmd.studentsclubactivities.ui.activity.ClubActivityDetailsActivity;
import gr.academic.city.sdmd.studentsclubactivities.util.Commons;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;

import static gr.academic.city.sdmd.studentsclubactivities.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/13/16.
 */
public class PushToServerService extends IntentService {

    private static final int NOTIFICATION_ID = 187;

    private static final String ACTION_PUSH_CLUB_ACTIVITIES_TO_SERVER = "gr.academic.city.sdmd.studentsclubactivities.ACTION_PUSH_CLUB_ACTIVITIES_TO_SERVER";

    public static void startPushClubActivitiesToServer(Context context) {
        Intent intent = new Intent(context, PushToServerService.class);
        intent.setAction(ACTION_PUSH_CLUB_ACTIVITIES_TO_SERVER);

        context.startService(intent);
    }

    public PushToServerService() {
        super("PushToServerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_PUSH_CLUB_ACTIVITIES_TO_SERVER.equals(intent.getAction())) {
            pushActivitiesToServer();
            deleteActivitiesToServer();
        }
    }

    private void pushActivitiesToServer() {
        Cursor cursor = getContentResolver().query(
                ClubManagementContract.ClubActivity.CONTENT_URI,
                null,
                ClubManagementContract.ClubActivity.COLUMN_NAME_UPLOADED_TO_SERVER + " = ?",
                new String[]{String.valueOf(0)},
                null);

        while (cursor.moveToNext()) {
            final long clubActivityDbId = cursor.getLong(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity._ID));
            long clubServerId = cursor.getLong(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_CLUB_SERVER_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE));
            String shortNote = cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE));
            String longNote = cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LONG_NOTE));
            long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP));
            double latitude =  cursor.getDouble(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LAT));
            double longitude =  cursor.getDouble(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LNG));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_LOCATION));

            executeRequest(MessageFormat.format(Constants.CLUB_ACTIVITIES_URL, clubServerId), Commons.ConnectionMethod.POST, new Gson().toJson(new ClubActivity(title, shortNote, longNote, timestamp, latitude, longitude, location, clubServerId)), new Commons.ResponseCallback() {
                @Override
                public void onResponse(int responseCode, String responsePayload) {
                    // responsePayload is the new ID of this club activity on the server

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_UPLOADED_TO_SERVER, 1);
                    contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_SERVER_ID, responsePayload);

                    getContentResolver().update(
                            ContentUris.withAppendedId(ClubManagementContract.ClubActivity.CONTENT_URI, clubActivityDbId),
                            contentValues,
                            null,
                            null
                    );

                    showNotification(clubActivityDbId);
                }
            });
        }

        cursor.close();
    }

    private void deleteActivitiesToServer() {
        Cursor cursor = getContentResolver().query(
                ClubManagementContract.ClubActivity.CONTENT_URI,
                null,
                ClubManagementContract.ClubActivity.COLUMN_NAME_UPLOADED_TO_SERVER + " = ?",
                new String[]{String.valueOf(-1)},
                null);

        while (cursor.moveToNext()) {
            final long clubActivityDbId = cursor.getLong(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity._ID));
            final long clubActivityServerId = cursor.getLong(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_SERVER_ID));
            final long clubServerId = cursor.getLong(cursor.getColumnIndexOrThrow(ClubManagementContract.ClubActivity.COLUMN_NAME_CLUB_SERVER_ID));

            executeRequest(MessageFormat.format(Constants.DELETE_CLUB_ACTIVITIES_URL, clubServerId, clubActivityServerId),
                    Commons.ConnectionMethod.DELETE,
                    null, new Commons.ResponseCallback() {
                        @Override
                        public void onResponse(int responseCode, String responsePayload) {
                            // responsePayload is the new ID of this club activity on the server

                            getContentResolver().delete(
                                    ContentUris.withAppendedId(ClubManagementContract.ClubActivity.CONTENT_URI, clubActivityDbId),
                                    null, null);

                            showDeleteNotification(clubServerId);
                        }
                    });
        }

        cursor.close();
    }

    private void showNotification(long clubActivityId) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, ClubActivityDetailsActivity.getStartIntent(this, clubActivityId), PendingIntent.FLAG_UPDATE_CURRENT);

        String text = getString(R.string.msg_activity_uploaded);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.activity_uploaded))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showDeleteNotification(long clubServerId) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, ClubActivitiesActivity.getStartIntent(this,clubServerId), 0);

        String text = getString(R.string.activity_deleted);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.activity_deleted))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
