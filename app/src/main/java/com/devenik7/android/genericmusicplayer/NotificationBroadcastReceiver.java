package com.devenik7.android.genericmusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nisha on 06-Aug-17.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(NotificationUtils.NOTIFICATION_SWIPED_ACTION)) {
            Intent intent1 = new Intent(context, MusicPlayerService.class);
            intent1.setAction(action);
            context.startService(intent1);
        }
    }
}
