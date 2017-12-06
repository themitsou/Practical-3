package gr.academic.city.sdmd.projectissues.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by trumpets on 4/13/16.
 */
public final class ProjectManagementContract {

    public static final String AUTHORITY = "gr.academic.city.sdsm.projectissues";

    // To prevent someone from accidentally instantiating the,
    // contract class, give it a private empty constructor.
    private ProjectManagementContract() {
    }

    // Inner class that defines the table contents
    // BaseColumns allow us to inherit a primary key field called _ID
    // that most Android classes expect
    public static abstract class Project implements BaseColumns {

        public static final String TABLE_NAME = "project";
        public static final String COLUMN_NAME_PROJECT_NAME = "project_name";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME);
    }

    public static abstract class ProjectIssue implements BaseColumns {

        public static final String TABLE_NAME = "project_issue";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SHORT_NOTE = "short_note";
        public static final String COLUMN_NAME_LONG_NOTE = "long_note";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_START_DATE = "start_date";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_ESTIMATED_HOURS = "estimated_hours";
        public static final String COLUMN_NAME_UPLOADED_TO_SERVER = "uploaded_to_server";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_PROJECT_SERVER_ID = "club_server_id";
        public static final String COLUMN_NAME_ASSIGNEE_SERVER_ID = "assignee_server_id";
        public static final String COLUMN_NAME_ASSIGNEE_NAME = "assignee_name";
        public static final String COLUMN_NAME_FOR_DELETION = "for_deletion";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME);
    }

    public static abstract class WorkLog implements BaseColumns {

        public static final String TABLE_NAME = "work_log";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_ISSUE_SERVER_ID = "issue_server_id";
        public static final String COLUMN_NAME_COMMENT = "comment";
        public static final String COLUMN_NAME_WORK_HOURS = "work_hours";
        public static final String COLUMN_NAME_UPLOADED_TO_SERVER = "uploaded_to_server";
        public static final String COLUMN_NAME_FOR_DELETION = "for_deletion";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME);
    }
}
