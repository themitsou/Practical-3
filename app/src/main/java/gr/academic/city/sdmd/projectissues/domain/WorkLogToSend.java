package gr.academic.city.sdmd.projectissues.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;

/**
 * Created by Theofanis on 8/8/2017.
 */

public class WorkLogToSend {
    @SerializedName("issue_id")
    private Long issue_id;

    @SerializedName("hours")
    private Long hours;

    @SerializedName("activity_id")
    private Long activity_id;

    @SerializedName("comments")
    private String comments;

    @SerializedName("spent_on")
    private String spent_on;



    public WorkLogToSend() {

    }

    public WorkLogToSend(Long issue_id, Long hours, String comments) {
        this.setIssue_id(issue_id);
        this.setHours(hours);
        this.setActivity_id(9L);
        this.setComments(comments);
        this.setSpent_on("2017-08-10");
    }

    public Long getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(Long issue_id) {
        this.issue_id = issue_id;
    }

    public Long getHours() {
        return hours;
    }

    public void setHours(Long hours) {
        this.hours = hours;
    }

    public Long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSpent_on() {
        return spent_on;
    }

    public void setSpent_on(String spent_on) {
        this.spent_on = spent_on;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_COMMENT, getComments());
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_ISSUE_SERVER_ID, getIssue_id());
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_WORK_HOURS, getHours());

        return contentValues;
    }
}

