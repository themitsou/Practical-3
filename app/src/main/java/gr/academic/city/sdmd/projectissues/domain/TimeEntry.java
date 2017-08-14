package gr.academic.city.sdmd.projectissues.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;

/**
 * Created by trumpets on 4/13/16.
 */
public class TimeEntry {

    @SerializedName("id")
    private long id;

    @SerializedName("project")
    private Project project;

    @SerializedName("issue")
    private Issue issue;

    @SerializedName("user")
    private User user;

    @SerializedName("activity")
    private Activity activity;

    @SerializedName("hours")
    private double hours;

    @SerializedName("comments")
    private String comments;

    @SerializedName("spent_on")
    private String spent_on;

    @SerializedName("created_on")
    private String created_on;

    @SerializedName("updated_on")
    private String updated_on;

    // Default empty constructor needed for serialization
    public TimeEntry() {

    }

    public TimeEntry(Long issueId, double hours, String comments) {
        this.setIssue(new Issue("","","","",issueId));
        this.setHours(hours);
        this.setComments(comments);

    }



    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_COMMENT, getComments());
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_ISSUE_SERVER_ID, getIssue().getServerId());
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_SERVER_ID, getId());
        contentValues.put(ProjectManagementContract.WorkLog.COLUMN_NAME_WORK_HOURS, getHours());

        return contentValues;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
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

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }
}
