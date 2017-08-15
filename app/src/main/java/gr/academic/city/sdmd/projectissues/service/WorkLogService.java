package gr.academic.city.sdmd.projectissues.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import java.text.MessageFormat;

import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.domain.Issue;
import gr.academic.city.sdmd.projectissues.domain.Issues;
import gr.academic.city.sdmd.projectissues.domain.TimeEntries;
import gr.academic.city.sdmd.projectissues.domain.TimeEntry;
import gr.academic.city.sdmd.projectissues.domain.WorkLogToSend;
import gr.academic.city.sdmd.projectissues.receiver.TriggerPushToServerBroadcastReceiver;
import gr.academic.city.sdmd.projectissues.util.Commons;
import gr.academic.city.sdmd.projectissues.util.Constants;
import gr.academic.city.sdmd.projectissues.util.GsonResponseCallback;

import static gr.academic.city.sdmd.projectissues.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/13/16.
 */
public class WorkLogService extends IntentService {

    private static final String ACTION_FETCH_WORK_LOGS = "gr.academic.city.sdmd.studentsclubactivities.FETCH_WORK_LOGS";
    private static final String EXTRA_WORK_LOG_SERVER_ID = "work_log_server_id";

    private static final String ACTION_CREATE_WORK_LOG = "gr.academic.city.sdmd.studentsclubactivities.CREATE_WORK_LOG";
    private static final String EXTRA_COMMENT = "comment";
    private static final String EXTRA_WORK_HOURS = "work_hours";
    private static final String EXTRA_TIMESTAMP = "timestamp";


    private static final String ACTION_DELETE_WORK_LOG = "gr.academic.city.sdmd.studentsclubactivities.DELETE_WORK_LOG";
    private static final String ACTION_UNDELETE_WORK_LOG = "gr.academic.city.sdmd.studentsclubactivities.UNDELETE_WORK_LOG";
    private static final String EXTRA_PROJECT_SERVER_ISSUE_ID = "project_server_issue_id";

    public static void startFetchWorkLogs(Context context) {
        Intent intent = new Intent(context, WorkLogService.class);
        intent.setAction(ACTION_FETCH_WORK_LOGS);

        context.startService(intent);
    }

    public static void startDeleteActivity(Context context,  long workLogId) {
        Intent intent = new Intent(context, WorkLogService.class);
        intent.setAction(ACTION_DELETE_WORK_LOG);
//        intent.putExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, clubServerActivityId);

        context.startService(intent);
    }

    public static void startUnDeleteActivity(Context context,  long workLogId) {
        Intent intent = new Intent(context, WorkLogService.class);
        intent.setAction(ACTION_UNDELETE_WORK_LOG);
//        intent.putExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, clubServerActivityId);

        context.startService(intent);
    }

    public static void startCreateWorkLog(Context context, long workLogID, String comment, double workHours) {
        Intent intent = new Intent(context, WorkLogService.class);
        intent.setAction(ACTION_CREATE_WORK_LOG);
        intent.putExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, workLogID);
        intent.putExtra(EXTRA_COMMENT, comment);
        intent.putExtra(EXTRA_WORK_HOURS, workHours);

        context.startService(intent);
    }

    public WorkLogService() {
        super("WorkLogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_FETCH_WORK_LOGS.equals(intent.getAction())) {
            fetchWorkLogs(intent);
        } else if (ACTION_CREATE_WORK_LOG.equals(intent.getAction())) {
            createWorkLog(intent);
        }else if(ACTION_DELETE_WORK_LOG.equals(intent.getAction())){
            deleteWorkLog(intent);
        }else if(ACTION_UNDELETE_WORK_LOG.equals(intent.getAction())){
            unDeleteWorkLog(intent);
        } else {
            throw new UnsupportedOperationException("Action not supported: " + intent.getAction());
        }
    }

    private void fetchWorkLogs(Intent intent) {

        executeRequest(Constants.WORK_LOGS_URL, Commons.ConnectionMethod.GET, null, new GsonResponseCallback<TimeEntries>(TimeEntries.class) {
            @Override
            protected void onResponse(int responseCode, TimeEntries timeEntries) {

                for (TimeEntry timeEntry : timeEntries.getTimeEntries()) {
                    if (getContentResolver().query(
                            ProjectManagementContract.WorkLog.CONTENT_URI,
                            new String[0],
                            ProjectManagementContract.WorkLog.COLUMN_NAME_SERVER_ID + " = ?",
                            new String[]{String.valueOf(timeEntry.getId())},
                            null).getCount() == 0) {

                        // this time entry is not in db, add it
                        ContentValues contentValues = timeEntry.toContentValues();
                        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_UPLOADED_TO_SERVER, 1);

                        getContentResolver().insert(
                                ProjectManagementContract.WorkLog.CONTENT_URI,
                                contentValues);
                    }
                }
            }
        });
    }

    private void createWorkLog(Intent intent) {
        long issueServerId = intent.getLongExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, -1);
        String comment = intent.getStringExtra(EXTRA_COMMENT);
        double workHours = intent.getDoubleExtra(EXTRA_WORK_HOURS,-1);



        ContentValues contentValues = new WorkLogToSend(issueServerId, workHours, comment).toContentValues();
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_UPLOADED_TO_SERVER, 0);
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_SERVER_ID, -1);
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_FOR_DELETION, 0);

        getContentResolver().insert(
                ProjectManagementContract.WorkLog.CONTENT_URI,
                contentValues
        );

        sendBroadcast(new Intent(TriggerPushToServerBroadcastReceiver.ACTION_TRIGGER));
    }

    private void deleteWorkLog (Intent intent) {
        long workLogServerId= intent.getLongExtra(EXTRA_WORK_LOG_SERVER_ID, -1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_FOR_DELETION, 1);

        getContentResolver().update(
                ContentUris.withAppendedId(ProjectManagementContract.WorkLog.CONTENT_URI, workLogServerId),
                contentValues,
                null,
                null
        );


    }
    private void unDeleteWorkLog (Intent intent) {
        long workLogServerId = intent.getLongExtra(EXTRA_WORK_LOG_SERVER_ID, -1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_FOR_DELETION, 0);

        getContentResolver().update(
                ContentUris.withAppendedId(ProjectManagementContract.WorkLog.CONTENT_URI, workLogServerId),
                contentValues,
                null,
                null
        );


    }

}
