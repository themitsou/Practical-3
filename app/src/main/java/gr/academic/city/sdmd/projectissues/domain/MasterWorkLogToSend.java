package gr.academic.city.sdmd.projectissues.domain;

/**
 * Created by Theofanis on 8/8/2017.
 */

public class MasterWorkLogToSend {
    private WorkLogToSend time_entry;

    public MasterWorkLogToSend(WorkLogToSend time_entry) {
        this.time_entry = time_entry;
    }

    public WorkLogToSend getWorkLog() {
        return time_entry;
    }

    public void setWorkLog(WorkLogToSend time_entry) {
        this.time_entry = time_entry;
    }
}