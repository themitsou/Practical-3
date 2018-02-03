package gr.academic.city.sdmd.projectissues.util;

/**
 * Created by tmitsou on 4/13/16.
 */
public final class Constants {

    private Constants() {}

//    public static final String SERVER_URL = "http://10.0.2.2:3000/";
//    public static final String SERVER_URL = "http://192.168.137.1:3000/";
    public static final String SERVER_URL = "http://192.168.43.152:3000/";
    public static final String PROJECTS_URL = SERVER_URL + "projects.json";
    public static final String PROJECT_ISSUES_URL = SERVER_URL + "issues.json?project_id={0}";
    public static final String PROJECT_ISSUE_URL = SERVER_URL + "issues.json";
    public static final String DELETE_PROJECT_ISSUES_URL = SERVER_URL + "issues/{0}.json";
    public static final String WORK_LOGS_URL = SERVER_URL + "time_entries.json";

    public static final String PROJECT_ISSUES_DATE_FORMAT = "dd-MM-yyyy";
}
