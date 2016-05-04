package gr.academic.city.sdmd.studentsclubactivities.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by trumpets on 4/13/16.
 */
public final class ClubManagementContract {

    public static final String AUTHORITY = "gr.academic.city.sdsm.studentsclubactivities";

    // To prevent someone from accidentally instantiating the,
    // contract class, give it a private empty constructor.
    private ClubManagementContract() {
    }

    // Inner class that defines the table contents
    // BaseColumns allow us to inherit a primary key field called _ID
    // that most Android classes expect
    public static abstract class Club implements BaseColumns {

        public static final String TABLE_NAME = "club";
        public static final String COLUMN_NAME_CLUB_NAME = "club_name";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME);
    }

    public static abstract class ClubActivity implements BaseColumns {

        public static final String TABLE_NAME = "club_activity";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SHORT_NOTE = "short_note";
        public static final String COLUMN_NAME_LONG_NOTE = "long_note";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
        public static final String COLUMN_NAME_LOCATION = "address";
        public static final String COLUMN_NAME_UPLOADED_TO_SERVER = "uploaded_to_server";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_CLUB_SERVER_ID = "club_server_id";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME);
    }
}
