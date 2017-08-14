package gr.academic.city.sdmd.projectissues.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;

/**
 * Created by trumpets on 4/13/16.
 */
public class Issue {

    private Tracker tracker;
    private Status status;
    private Author author;
    private String start_date;
    private String done_ratio;
    private double estimated_hours;
    private String created_on;

    @SerializedName("subject")
    private String subject;

    @SerializedName("description")
    private String description;


    @SerializedName("updated_on")
    private String timestamp;

    @SerializedName("id")
    private long id;

    @SerializedName("project")
    private Project project;

    // Default empty constructor needed for serialization
    public Issue() {

    }

    public Issue(String title, String shortNote, String longNote, String timestamp, long projectServerId) {
        this.setSubject(title);
        this.setDescription(shortNote);
        this.setTimestamp(timestamp);
        this.setProject(new Project(projectServerId));
    }



    public long getServerId() {
        return getId();
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDone_ratio() {
        return done_ratio;
    }

    public void setDone_ratio(String done_ratio) {
        this.done_ratio = done_ratio;
    }

    public double getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(Double estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_TITLE, getSubject());
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SHORT_NOTE, getDescription());
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_TIMESTAMP, getTimestamp());
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_ESTIMATED_HOURS, getEstimated_hours());
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID, getId());
        contentValues.put(ProjectManagementContract.ProjectIssue.COLUMN_NAME_PROJECT_SERVER_ID, getProject().getServerId());

        return contentValues;
    }
}
