package com.devenik7.android.genericmusicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;

/**
 * Created by nisha on 06-Aug-17.
 */

public class NotificationUtils {

    public static final int FOREGROUND_NOTIFICATION_ID = 12345;
    public static final String NOTIFICATION_SWIPED_ACTION = "swiped";

    public static Notification playNotification(Context context, String Title, String Artist) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(Title)
                .setContentText(Artist)
                .setOngoing(true)
                .setUsesChronometer(true)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(previousAction(context))
                .addAction(pauseAction(context))
                .addAction(nextAction(context))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(swipedIntent(context));

        return builder.build();
    }

    public static void showPausedNotification(Context context, String Title, String Artist) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(Title)
                .setContentText(Artist)
                .setOngoing(false)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(previousAction(context))
                .addAction(playAction(context))
                .addAction(nextAction(context))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(swipedIntent(context));

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(FOREGROUND_NOTIFICATION_ID, builder.build());
    }

    public static void clearNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(FOREGROUND_NOTIFICATION_ID);
    }

    private static Action playAction(Context context) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.PLAY_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(android.R.drawable.ic_media_play, "", pendingIntent);
    }

    private static Action pauseAction(Context context) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.PAUSE_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(android.R.drawable.ic_media_pause, "", pendingIntent);
    }

    private static Action previousAction(Context context) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.PREVIOUS_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(android.R.drawable.ic_media_previous, "", pendingIntent);
    }

    private static Action nextAction(Context context) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.NEXT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(android.R.drawable.ic_media_next, "", pendingIntent);
    }

    private static PendingIntent swipedIntent(Context context) {
        Intent intent = new Intent(context,NotificationBroadcastReceiver.class);
        intent.setAction(NotificationUtils.NOTIFICATION_SWIPED_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
