package com.devenik7.android.genericmusicplayer;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by nisha on 06-Aug-17.
 */

public class NotificationUtils {

    public static final int FOREGROUND_NOTIFICATION_ID = 12345;

    public static Notification playNotification (Context context, String Title, String Artist) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(Title)
                .setContentText(Artist)
                .setOngoing(true)
                .setUsesChronometer(true)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

}
