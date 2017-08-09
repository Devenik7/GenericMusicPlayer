package com.devenik7.android.genericmusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by nisha on 09-Aug-17.
 */

public class PlayerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case MusicPlayerUtils.UPDATE_INFO_BROADCAST:
                String title = intent.getStringExtra(PlayerContract.MusicEntry.MUSIC_TITLE);
                String artist = intent.getStringExtra(PlayerContract.MusicEntry.MUSIC_ARTIST);
                boolean isPlaying = intent.getBooleanExtra(MusicPlayerUtils.IS_PLAYING, false);
                ((MainActivity) context).receiveUpdateBroadcast(title, artist, isPlaying);
                break;
        }
    }
}
