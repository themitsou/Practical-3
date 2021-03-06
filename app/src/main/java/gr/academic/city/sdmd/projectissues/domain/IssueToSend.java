package gr.academic.city.sdmd.projectissues.domain;

/**
 * Created by Theofanis on 8/8/2017.
 */

public class IssueToSend {
    private Long project_id;
    private Long tracker_id;
    private Long status_id;
    private Long priority_id;
    private Long estimated_hours;
    private String description;
    private String subject;
    private Long assigned_to_id;

    public IssueToSend() {

    }

    public IssueToSend(Long project_id, String description, String subject, Long assigned_to_id) {
        this.project_id = project_id;
        this.description = description;
        this.subject = subject;
        this.setAssigned_to_id(assigned_to_id);
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public Long getTracker_id() {
        return tracker_id;
    }

    public void setTracker_id(Long tracker_id) {
        this.tracker_id = tracker_id;
    }

    public Long getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Long status_id) {
        this.status_id = status_id;
    }

    public Long getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(Long estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getPriority_id() {
        return priority_id;
    }

    public void setPriority_id(Long priority_id) {
        this.priority_id = priority_id;
    }

    public Long getAssigned_to_id() {
        return assigned_to_id;
    }

    public void setAssigned_to_id(Long assigned_to_id) {
        this.assigned_to_id = assigned_to_id;
    }
}

