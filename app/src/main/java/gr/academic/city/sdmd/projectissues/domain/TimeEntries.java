package gr.academic.city.sdmd.projectissues.domain;

/**
 * Created by Theofanis on 8/8/2017.
 */

public class TimeEntries {
    private TimeEntry[] timeEntries;
    private Long total_count;
    private Long offset;
    private Long limit;


    public TimeEntry[] getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(TimeEntry[] timeEntries) {
        this.timeEntries = timeEntries;
    }

    public Long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Long total_count) {
        this.total_count = total_count;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public TimeEntry TimeEntry(Integer i) {
        return getTimeEntries()[i];
    }

}

