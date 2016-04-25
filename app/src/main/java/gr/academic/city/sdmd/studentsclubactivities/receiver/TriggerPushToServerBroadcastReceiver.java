package gr.academic.city.sdmd.studentsclubactivities.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import gr.academic.city.sdmd.studentsclubactivities.service.PushToServerService;

/**
 * Created by trumpets on 4/13/16.
 */
public class TriggerPushToServerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TRIGGER = "gr.academic.city.sdmd.studentsclubactivities.TRIGGER";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            PushToServerService.startPushClubActivitiesToServer(context);
        }
    }
}
