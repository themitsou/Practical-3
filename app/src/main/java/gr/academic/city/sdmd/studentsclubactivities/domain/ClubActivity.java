package gr.academic.city.sdmd.studentsclubactivities.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubActivity {

    @SerializedName("title")
    private String title;

    @SerializedName("shortNote")
    private String shortNote;

    @SerializedName("longNote")
    private String longNote;

    @SerializedName("date")
    private long timestamp;

    @SerializedName("id")
    private long serverId;

    @SerializedName("club")
    private Club club;

    // Default empty constructor needed for serialization
    public ClubActivity() {

    }

    public ClubActivity(String title, String shortNote, String longNote, long timestamp, long clubServerId) {
        this.title = title;
        this.shortNote = shortNote;
        this.longNote = longNote;
        this.timestamp = timestamp;
        this.club = new Club(clubServerId);
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_TITLE, title);
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_SHORT_NOTE, shortNote);
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_LONG_NOTE, longNote);
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_TIMESTAMP, timestamp);
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_SERVER_ID, serverId);
        contentValues.put(ClubManagementContract.ClubActivity.COLUMN_NAME_CLUB_SERVER_ID, club.getServerId());

        return contentValues;
    }

    public long getServerId() {
        return serverId;
    }
}
