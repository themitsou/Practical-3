package gr.academic.city.sdmd.projectissues.service;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

import gr.academic.city.sdmd.projectissues.R;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.domain.Issue;
import gr.academic.city.sdmd.projectissues.domain.MasterWorkLogToSend;
import gr.academic.city.sdmd.projectissues.domain.WorkLogToSend;
import gr.academic.city.sdmd.projectissues.ui.activity.ClubActivitiesActivity;
import gr.academic.city.sdmd.projectissues.ui.activity.ClubActivityDetailsActivity;
import gr.academic.city.sdmd.projectissues.util.Commons;
import gr.academic.city.sdmd.projectissues.util.Constants;
import gr.academic.city.sdmd.projectissues.domain.IssueToSend;
import gr.academic.city.sdmd.projectissues.domain.MasterIssueToSend;

import static gr.academic.city.sdmd.projectissues.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/13/16.
 */
public class PushToServerService extends IntentService {

    private static final int NOTIFICATION_ID = 187;

    private static final String ACTION_PUSH_PROJECT_ISSUES_TO_SERVER = "gr.academic.city.sdmd.projectissues.ACTION_PUSH_PROJECT_ISSUES_TO_SERVER";

    public static void startPushProjectIssuesToServer(Context context) {
        Intent intent = new Intent(context, PushToServerService.class);
        intent.setAction(ACTION_PUSH_PROJECT_ISSUES_TO_SERVER);

        context.startService(intent);
    }

    public PushToServerService() {
        super("PushToServerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_PUSH_PROJECT_ISSUES_TO_SERVER.equals(intent.getAction())) {
            pushActivitiesToServer();
            deleteActivitiesToServer();
            pushWorkLogsToServer();
        }
    }

    private void pushActivitiesToServer() {
        Cursor cursor = getContentResolver().query(
                ProjectManagementContract.ProjectIssue.CONTENT_URI,
                null,
                ProjectManagementContract.ProjectIssue.COLUMN_NAME_UPLOADED_TO_SERVER + " = ?",
                new String[]{String.valueOf(0)},
                null);

        while (cursor.moveToNext()) {
            final long clubActivityDbId = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue._ID));
            long projectServerId = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_PROJECT_SERVER_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_TITLE));
            String shortNote = cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SHORT_NOTE));
            String longNote = cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_LONG_NOTE));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_TIMESTAMP));

            executeRequest(Constants.PROJECT_ISSUE_URL, Commons.ConnectionMethod.POST, new Gson().toJson(new MasterIssueToSend(new IssueToSend(projectServerId, title, shortNote))), new Commons.ResponseCallback() {
                @Override
                public void onResponse(int responseCode, String responsePayload) {
                    String serverID ="-1";
                    try {
                        JSONObject json = new JSONObject(responsePayload);
                        serverID = json.getJSONObject("issue").getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // responsePayload is the new ID of this club activity on the server

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_UPLOADED_TO_SERVER, 1);
                    contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID, serverID);

                    getContentResolver().update(
                            ContentUris.withAppendedId(ProjectManagementContract.ProjectIssue.CONTENT_URI, clubActivityDbId),
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
                ProjectManagementContract.ProjectIssue.CONTENT_URI,
                null,
                ProjectManagementContract.ProjectIssue.COLUMN_NAME_FOR_DELETION + " = ?",
                new String[]{String.valueOf(1)},
                null);

        while (cursor.moveToNext()) {
            final long  projectIssueDbId= cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue._ID));
            final long projectIssueServerId = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID));
            final long projectServerId = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_PROJECT_SERVER_ID));

            executeRequest(MessageFormat.format(Constants.DELETE_PROJECT_ISSUES_URL, projectIssueServerId),
                    Commons.ConnectionMethod.DELETE,
                    null, new Commons.ResponseCallback() {
                        @Override
                        public void onResponse(int responseCode, String responsePayloprojectIssueDbIdad) {
                            // responsePayload is the new ID of this project issue on the server

                            getContentResolver().delete(
                                    ContentUris.withAppendedId(ProjectManagementContract.ProjectIssue.CONTENT_URI, projectIssueDbId),
                                    null, null);

                            showDeleteNotification(projectServerId);
                        }
                    });
        }

        cursor.close();
    }

    private void pushWorkLogsToServer() {
        Cursor cursor = getContentResolver().query(
                ProjectManagementContract.WorkLog.CONTENT_URI,
                null,
                ProjectManagementContract.WorkLog.COLUMN_NAME_UPLOADED_TO_SERVER + " = ?",
                new String[]{String.valueOf(0)},
                null);

        while (cursor.moveToNext()) {
            final long WorklogDbId = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.WorkLog._ID));
            long issueServerId = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.WorkLog.COLUMN_NAME_ISSUE_SERVER_ID));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.WorkLog.COLUMN_NAME_COMMENT));
            long hours = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.WorkLog.COLUMN_NAME_WORK_HOURS));

            executeRequest(Constants.WORK_LOGS_URL, Commons.ConnectionMethod.POST, new Gson().toJson(new MasterWorkLogToSend((new WorkLogToSend(issueServerId, hours, comment)))), new Commons.ResponseCallback() {
                @Override
                public void onResponse(int responseCode, String responsePayload) {
                    String serverID ="-1";
                    try {
                        JSONObject json = new JSONObject(responsePayload);
                        serverID = json.getJSONObject("time_entry").getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // responsePayload is the new ID of this club activity on the server

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_UPLOADED_TO_SERVER, 1);
                    contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_SERVER_ID, serverID);

                    getContentResolver().update(
                            ContentUris.withAppendedId(ProjectManagementContract.WorkLog.CONTENT_URI, WorklogDbId),
                            contentValues,
                            null,
                            null
                    );

                    showWorkLogNotification(WorklogDbId);
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

    private void showWorkLogNotification(long workLogId) {
       // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, ClubActivityDetailsActivity.getStartIntent(this, clubActivityId), PendingIntent.FLAG_UPDATE_CURRENT);

        String text = getString(R.string.msg_work_log_uploaded);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.msg_work_log_uploaded))
                .setContentText(text)
                //.setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
