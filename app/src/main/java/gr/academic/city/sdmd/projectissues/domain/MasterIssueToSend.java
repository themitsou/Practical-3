package gr.academic.city.sdmd.projectissues.domain;

/**
 * Created by Theofanis on 8/8/2017.
 */

public class MasterIssueToSend {
    private IssueToSend issue;

    public MasterIssueToSend(IssueToSend issue) {
        this.issue = issue;
    }

    public IssueToSend getIssue() {
        return issue;
    }

    public void setIssue(IssueToSend issue) {
        this.issue = issue;
    }
}