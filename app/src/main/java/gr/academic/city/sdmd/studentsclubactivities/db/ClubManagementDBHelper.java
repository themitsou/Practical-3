package gr.academic.city.sdmd.studentsclubactivities.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubManagementDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ClubManagement.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String SHORT_TYPE = " SHORT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_CLUBS =
            "CREATE TABLE " + ClubManagementContract.Club.TABLE_NAME + " (" +
                    ClubManagementContract.Club._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ClubManagementContract.Club.COLUMN_NAME_CLUB_NAME + TEXT_TYPE + COMMA_SEP +
                    ClubManagementContract.Club.COLUMN_NAME_SERVER_ID + INT_TYPE +
                    " )";

    private static final String SQL_CREATE_CLUB_ACTIVITIES =
            "CREATE TABLE " + ClubManagementContract.ClubActivity.TABLE_NAME + " (" +
                    ClubManagementContract.ClubActivity._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE + TEXT_TYPE + COMMA_SEP +
                    ClubManagementContract.ClubActivity.COLUMN_NAME_LONG_NOTE + TEXT_TYPE + COMMA_SEP +
                    ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP + INT_TYPE + COMMA_SEP +
                    ClubManagementContract.ClubActivity.COLUMN_NAME_UPLOADED_TO_SERVER + SHORT_TYPE + COMMA_SEP +
                    ClubManagementContract.ClubActivity.COLUMN_NAME_SERVER_ID + INT_TYPE + COMMA_SEP +
                    ClubManagementContract.ClubActivity.COLUMN_NAME_CLUB_SERVER_ID + INT_TYPE +
                    " )";

    private static final String SQL_DELETE_CLUBS =
            "DROP TABLE IF EXISTS " + ClubManagementContract.Club.TABLE_NAME;

    private static final String SQL_DELETE_CLUB_ACTIVITIES =
            "DROP TABLE IF EXISTS " + ClubManagementContract.Club.TABLE_NAME;

    public ClubManagementDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CLUBS);
        db.execSQL(SQL_CREATE_CLUB_ACTIVITIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade
        // policy is to simply discard the data and start over
        // although one can argue that we should prevent the deletion of
        // data still not sent to the server
        // However, that is not the scope of this coursework
        db.execSQL(SQL_DELETE_CLUBS);
        db.execSQL(SQL_DELETE_CLUB_ACTIVITIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
