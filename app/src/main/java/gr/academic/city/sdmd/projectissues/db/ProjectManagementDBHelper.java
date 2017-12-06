package gr.academic.city.sdmd.projectissues.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by trumpets on 4/13/16.
 */
public class ProjectManagementDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ProjectManagement.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String SHORT_TYPE = " SHORT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";
    private static final String DEFAULT_0 = " DEFAULT (0)";

    private static final String SQL_CREATE_PROJECTS =
            "CREATE TABLE " + ProjectManagementContract.Project.TABLE_NAME + " (" +
                    ProjectManagementContract.Project._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ProjectManagementContract.Project.COLUMN_NAME_PROJECT_NAME + TEXT_TYPE + COMMA_SEP +
                    ProjectManagementContract.Project.COLUMN_NAME_SERVER_ID + INT_TYPE +
                    " )";

    private static final String SQL_CREATE_PROJECT_ISSUES =
            "CREATE TABLE " + ProjectManagementContract.ProjectIssue.TABLE_NAME + " (" +
                    ProjectManagementContract.ProjectIssue._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_SHORT_NOTE + TEXT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_LONG_NOTE + TEXT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_TIMESTAMP + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_START_DATE + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_DUE_DATE + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_ESTIMATED_HOURS + DOUBLE_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_UPLOADED_TO_SERVER + SHORT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_PROJECT_SERVER_ID + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_ASSIGNEE_SERVER_ID + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_ASSIGNEE_NAME + TEXT_TYPE + COMMA_SEP +
                    ProjectManagementContract.ProjectIssue.COLUMN_NAME_FOR_DELETION + SHORT_TYPE + DEFAULT_0 +
                    " )";

    private static final String SQL_CREATE_WORK_LOGS =
            "CREATE TABLE " + ProjectManagementContract.WorkLog.TABLE_NAME + " (" +
                    ProjectManagementContract.WorkLog._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ProjectManagementContract.WorkLog.COLUMN_NAME_ISSUE_SERVER_ID + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.WorkLog.COLUMN_NAME_SERVER_ID + INT_TYPE + COMMA_SEP +
                    ProjectManagementContract.WorkLog.COLUMN_NAME_COMMENT + TEXT_TYPE + COMMA_SEP +
                    ProjectManagementContract.WorkLog.COLUMN_NAME_WORK_HOURS + DOUBLE_TYPE + COMMA_SEP +
                    ProjectManagementContract.WorkLog.COLUMN_NAME_UPLOADED_TO_SERVER + SHORT_TYPE + COMMA_SEP +
                    ProjectManagementContract.WorkLog.COLUMN_NAME_FOR_DELETION + SHORT_TYPE + DEFAULT_0 +
                    " )";

    private static final String SQL_DELETE_PROJECTS =
            "DROP TABLE IF EXISTS " + ProjectManagementContract.Project.TABLE_NAME;

    private static final String SQL_DELETE_PROJECT_ISSUES =
            "DROP TABLE IF EXISTS " + ProjectManagementContract.ProjectIssue.TABLE_NAME;

    private static final String SQL_DELETE_WORK_LOGS =
            "DROP TABLE IF EXISTS " + ProjectManagementContract.WorkLog.TABLE_NAME;

    public ProjectManagementDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PROJECTS);
        db.execSQL(SQL_CREATE_PROJECT_ISSUES);
        db.execSQL(SQL_CREATE_WORK_LOGS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade
        // policy is to simply discard the data and start over
        // although one can argue that we should prevent the deletion of
        // data still not sent to the server
        // However, that is not the scope of this coursework
        db.execSQL(SQL_DELETE_PROJECTS);
        db.execSQL(SQL_DELETE_PROJECT_ISSUES);
        db.execSQL(SQL_DELETE_WORK_LOGS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
