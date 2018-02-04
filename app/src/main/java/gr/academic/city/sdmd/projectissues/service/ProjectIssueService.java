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
import gr.academic.city.sdmd.projectissues.receiver.TriggerPushToServerBroadcastReceiver;
import gr.academic.city.sdmd.projectissues.util.Commons;
import gr.academic.city.sdmd.projectissues.util.Constants;
import gr.academic.city.sdmd.projectissues.util.GsonResponseCallback;

import static gr.academic.city.sdmd.projectissues.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/13/16.
 */
public class ProjectIssueService extends IntentService {

    private static final String ACTION_FETCH_PROJECT_ISSUES = "gr.academic.city.sdmd.studentsclubactivities.FETCH_ACTIVITIES";
    private static final String EXTRA_PROJECT_SERVER_ID = "club_server_id";

    private static final String ACTION_CREATE_PROJECT_ISSUE = "gr.academic.city.sdmd.studentsclubactivities.CREATE_CLUB_ACTIVITY";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_SHORT_NOTE = "short_note";
    private static final String EXTRA_LONG_NOTE = "long_note";
    private static final String EXTRA_TIMESTAMP = "timestamp";
    private static final String EXTRA_START_DATE = "start_date";
    private static final String EXTRA_DUE_DATE = "due_date";


    private static final String ACTION_DELETE_PROJECT_ISSUE = "gr.academic.city.sdmd.studentsclubactivities.DELETE_CLUB_ACTIVITY";
    private static final String ACTION_UNDELETE_PROJECT_ISSUE = "gr.academic.city.sdmd.studentsclubactivities.UNDELETE_CLUB_ACTIVITY";
    private static final String EXTRA_PROJECT_SERVER_ISSUE_ID = "project_server_issue_id";

    public static void startFetchActivities(Context context,  long clubServerId) {
        Intent intent = new Intent(context, ProjectIssueService.class);
        intent.setAction(ACTION_FETCH_PROJECT_ISSUES);
        intent.putExtra(EXTRA_PROJECT_SERVER_ID, clubServerId);

        context.startService(intent);
    }

    public static void startDeleteActivity(Context context,  long clubServerActivityId) {
        Intent intent = new Intent(context, ProjectIssueService.class);
        intent.setAction(ACTION_DELETE_PROJECT_ISSUE);
        intent.putExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, clubServerActivityId);

        context.startService(intent);
    }

    public static void startUnDeleteActivity(Context context,  long clubServerActivityId) {
        Intent intent = new Intent(context, ProjectIssueService.class);
        intent.setAction(ACTION_UNDELETE_PROJECT_ISSUE);
        intent.putExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, clubServerActivityId);

        context.startService(intent);
    }

    public static void startCreateProjectIssue(Context context, long clubServerId, String title, String shortNote, String longNote, String timestamp) {
        Intent intent = new Intent(context, ProjectIssueService.class);
        intent.setAction(ACTION_CREATE_PROJECT_ISSUE);
        intent.putExtra(EXTRA_PROJECT_SERVER_ID, clubServerId);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_SHORT_NOTE, shortNote);
        intent.putExtra(EXTRA_LONG_NOTE, longNote);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        intent.putExtra(EXTRA_START_DATE, timestamp);
        intent.putExtra(EXTRA_DUE_DATE, timestamp);




        context.startService(intent);
    }

    public ProjectIssueService() {
        super("ProjectIssueService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_FETCH_PROJECT_ISSUES.equals(intent.getAction())) {
            fetchActivities(intent);
        } else if (ACTION_CREATE_PROJECT_ISSUE.equals(intent.getAction())) {
            createProjectIssue(intent);
        }else if(ACTION_DELETE_PROJECT_ISSUE.equals(intent.getAction())){
            deleteProjectIssue(intent);
        }else if(ACTION_UNDELETE_PROJECT_ISSUE.equals(intent.getAction())){
            unDeleteProjectIssue(intent);
        } else {
            throw new UnsupportedOperationException("Action not supported: " + intent.getAction());
        }
    }

    private void fetchActivities(Intent intent) {
        long projectServerId = intent.getLongExtra(EXTRA_PROJECT_SERVER_ID, -1);
        if (projectServerId ==-1){
            executeRequest(Constants.PROJECT_ISSUE_URL, Commons.ConnectionMethod.GET, null, new GsonResponseCallback<Issues>(Issues.class) {
                @Override
                protected void onResponse(int responseCode, Issues projectIssues) {

                    for (Issue issue : projectIssues.getIssues()) {
                        if (getContentResolver().query(
                                ProjectManagementContract.ProjectIssue.CONTENT_URI,
                                new String[0],
                                ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID + " = ?",
                                new String[]{String.valueOf(issue.getServerId())},
                                null).getCount() == 0) {

                            // this club activity is not in db, add it
                            ContentValues contentValues = issue.toContentValues();
                            contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_UPLOADED_TO_SERVER, 1);

                            getContentResolver().insert(
                                    ProjectManagementContract.ProjectIssue.CONTENT_URI,
                                    contentValues);
                        }
                    }
                }
            });


        }else {

            executeRequest(MessageFormat.format(Constants.PROJECT_ISSUES_URL, projectServerId), Commons.ConnectionMethod.GET, null, new GsonResponseCallback<Issues>(Issues.class) {
                @Override
                protected void onResponse(int responseCode, Issues projectIssues) {

                    for (Issue issue : projectIssues.getIssues()) {
                        if (getContentResolver().query(
                                ProjectManagementContract.ProjectIssue.CONTENT_URI,
                                new String[0],
                                ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID + " = ?",
                                new String[]{String.valueOf(issue.getServerId())},
                                null).getCount() == 0) {

                            // this club activity is not in db, add it
                            ContentValues contentValues = issue.toContentValues();
                            contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_UPLOADED_TO_SERVER, 1);

                            getContentResolver().insert(
                                    ProjectManagementContract.ProjectIssue.CONTENT_URI,
                                    contentValues);
                        }
                    }
                }
            });
        }
    }

    private void createProjectIssue(Intent intent) {
        long clubServerId = intent.getLongExtra(EXTRA_PROJECT_SERVER_ID, -1);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String shortNote = intent.getStringExtra(EXTRA_SHORT_NOTE);
        String longNote = intent.getStringExtra(EXTRA_LONG_NOTE);
        String timestamp = intent.getStringExtra(EXTRA_TIMESTAMP);
        String startDate = intent.getStringExtra(EXTRA_TIMESTAMP);
        String dueDate = intent.getStringExtra(EXTRA_TIMESTAMP);


        ContentValues contentValues = new Issue(title, shortNote, longNote, timestamp, startDate, dueDate, clubServerId).toCreateContentValues();
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_UPLOADED_TO_SERVER, 0);
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID, -1);
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_FOR_DELETION, 0);
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_ASSIGNEE_SERVER_ID, 5);

        getContentResolver().insert(
                ProjectManagementContract.ProjectIssue.CONTENT_URI,
                contentValues
                );

        sendBroadcast(new Intent(TriggerPushToServerBroadcastReceiver.ACTION_TRIGGER));
    }

    private void deleteProjectIssue (Intent intent) {
        long clubServerActivityId = intent.getLongExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, -1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_FOR_DELETION, 1);

        getContentResolver().update(
                ContentUris.withAppendedId(ProjectManagementContract.ProjectIssue.CONTENT_URI, clubServerActivityId),
                contentValues,
                null,
                null
        );


    }
    private void unDeleteProjectIssue (Intent intent) {
        long clubServerActivityId = intent.getLongExtra(EXTRA_PROJECT_SERVER_ISSUE_ID, -1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_FOR_DELETION, 0);

        getContentResolver().update(
                ContentUris.withAppendedId(ProjectManagementContract.ProjectIssue.CONTENT_URI, clubServerActivityId),
                contentValues,
                null,
                null
        );


    }

}
