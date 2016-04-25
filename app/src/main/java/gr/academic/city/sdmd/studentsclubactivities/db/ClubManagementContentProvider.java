package gr.academic.city.sdmd.studentsclubactivities.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubManagementContentProvider extends ContentProvider {

    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher;

    private static final int CLUBS = 1;
    private static final int CLUB_ITEM = 2;

    private static final int CLUB_ACTIVITIES = 11;
    private static final int CLUB_ACTIVITY_ITEM = 12;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ClubManagementContract.AUTHORITY, ClubManagementContract.Club.TABLE_NAME, CLUBS);
        uriMatcher.addURI(ClubManagementContract.AUTHORITY, ClubManagementContract.Club.TABLE_NAME + "/#", CLUB_ITEM);
        uriMatcher.addURI(ClubManagementContract.AUTHORITY, ClubManagementContract.ClubActivity.TABLE_NAME, CLUB_ACTIVITIES);
        uriMatcher.addURI(ClubManagementContract.AUTHORITY, ClubManagementContract.ClubActivity.TABLE_NAME + "/#", CLUB_ACTIVITY_ITEM);
    }

    private SQLiteOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new ClubManagementDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case CLUB_ITEM:
                selection = ClubManagementContract.Club._ID + "=" + uri.getLastPathSegment();
            case CLUBS:
                tableName = ClubManagementContract.Club.TABLE_NAME;
                break;
            case CLUB_ACTIVITY_ITEM:
                selection = ClubManagementContract.ClubActivity._ID + "=" + uri.getLastPathSegment();
            case CLUB_ACTIVITIES:
                tableName = ClubManagementContract.ClubActivity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs,
                null, null, sortOrder);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != CLUBS && uriMatcher.match(uri) != CLUB_ACTIVITIES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Uri contentUri = null;
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case CLUBS:
                contentUri = ClubManagementContract.Club.CONTENT_URI;
                tableName = ClubManagementContract.Club.TABLE_NAME;
                break;
            case CLUB_ACTIVITIES:
                contentUri = ClubManagementContract.ClubActivity.CONTENT_URI;
                tableName = ClubManagementContract.ClubActivity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Completely Unknown URI " + uri);
        }

        // Get the database and insert
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        if (rowId > 0) {
            Uri newUri = ContentUris.withAppendedId(contentUri, rowId);

            // Signal all cursor which monitor this URI that there is new data and
            // they should re-query
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Failed to insert row into " + uri);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case CLUB_ITEM:
                selection = ClubManagementContract.Club._ID + "=" + uri.getLastPathSegment();
            case CLUBS:
                tableName = ClubManagementContract.Club.TABLE_NAME;
                break;
            case CLUB_ACTIVITY_ITEM:
                selection = ClubManagementContract.ClubActivity._ID + "=" + uri.getLastPathSegment();
            case CLUB_ACTIVITIES:
                tableName = ClubManagementContract.ClubActivity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and update
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(tableName, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case CLUB_ITEM:
                selection = ClubManagementContract.Club._ID + "=" + uri.getLastPathSegment();
            case CLUBS:
                tableName = ClubManagementContract.Club.TABLE_NAME;
                break;
            case CLUB_ACTIVITY_ITEM:
                selection = ClubManagementContract.ClubActivity._ID + "=" + uri.getLastPathSegment();
            case CLUB_ACTIVITIES:
                tableName = ClubManagementContract.ClubActivity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and delete
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(tableName, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return count;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        String subType;
        String tableName;
        switch (uriMatcher.match(uri)) {
            case CLUB_ITEM:
                subType = "vnd.android.cursor.item/";
            case CLUBS:
                tableName = ClubManagementContract.Club.TABLE_NAME;
                subType = "vnd.android.cursor.dir/";
                break;
            case CLUB_ACTIVITY_ITEM:
                subType = "vnd.android.cursor.item/";
            case CLUB_ACTIVITIES:
                tableName = ClubManagementContract.ClubActivity.TABLE_NAME;
                subType = "vnd.android.cursor.dir/";
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return subType += "vnd." + ClubManagementContract.AUTHORITY + "." + tableName;

    }
}
