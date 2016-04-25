package gr.academic.city.sdmd.studentsclubactivities.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.domain.Club;
import gr.academic.city.sdmd.studentsclubactivities.util.Commons;
import gr.academic.city.sdmd.studentsclubactivities.util.Constants;
import gr.academic.city.sdmd.studentsclubactivities.util.GsonResponseCallback;

import static gr.academic.city.sdmd.studentsclubactivities.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/13/16.
 */
public class ClubService extends IntentService {

    private static final String ACTION_FETCH_CLUBS = "gr.academic.city.sdmd.studentsclubactivities.FETCH_CLUBS";

    public static void startFetchClubs(Context context) {
        Intent intent = new Intent(context, ClubService.class);
        intent.setAction(ACTION_FETCH_CLUBS);

        context.startService(intent);
    }

    public ClubService() {
        super("ClubService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_FETCH_CLUBS.equals(intent.getAction())) {
            fetchClubs();
        } else {
            throw new UnsupportedOperationException("Action not supported: " + intent.getAction());
        }
    }

    private void fetchClubs() {
        executeRequest(Constants.CLUBS_URL, Commons.ConnectionMethod.GET, null, new GsonResponseCallback<Club[]>(Club[].class) {
            @Override
            protected void onResponse(int responseCode, Club[] clubs) {
                for (Club club : clubs) {
                    if (getContentResolver().query(
                            ClubManagementContract.Club.CONTENT_URI,
                            null,
                            ClubManagementContract.Club.COLUMN_NAME_SERVER_ID + " = ?",
                            new String[]{String.valueOf(club.getServerId())},
                            null).getCount() == 0) {

                        // this club is not in db, add it
                        getContentResolver().insert(
                                ClubManagementContract.Club.CONTENT_URI,
                                club.toContentValues());
                    }
                }
            }
        });
    }
}
