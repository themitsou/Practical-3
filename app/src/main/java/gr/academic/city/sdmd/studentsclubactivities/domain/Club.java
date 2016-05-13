package gr.academic.city.sdmd.studentsclubactivities.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;

/**
 * Created by trumpets on 4/13/16.
 */
public class Club {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private long serverId;

    // Default empty constructor needed for serialization
    public Club() {

    }

    public Club(long serverId) {
        this.serverId = serverId;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ClubManagementContract.Club.COLUMN_NAME_CLUB_NAME, name);
        contentValues.put(ClubManagementContract.Club.COLUMN_NAME_SERVER_ID, serverId);

        return contentValues;
    }

    public long getServerId() {
        return serverId;
    }

    public String toString() {
        return name;
    }
}
