package gr.academic.city.sdmd.projectissues.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;

/**
 * Created by trumpets on 4/13/16.
 */
public class Project {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private long serverId;

    // Default empty constructor needed for serialization
    public Project() {

    }

    public Project(long serverId) {
        this.serverId = serverId;
    }

    public long getServerId() {
        return serverId;
    }

    public String toString() {
        return name;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ProjectManagementContract.Project.COLUMN_NAME_PROJECT_NAME, name);
        contentValues.put(ProjectManagementContract.Project.COLUMN_NAME_SERVER_ID, serverId);

        return contentValues;
    }
}
