package com.devenik7.android.genericmusicplayer.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devenik7.android.genericmusicplayer.activities.MainActivity;
import com.devenik7.android.genericmusicplayer.PlayerContract;
import com.devenik7.android.genericmusicplayer.activities.PlaylistActivity;
import com.devenik7.android.genericmusicplayer.utilities.MusicPlayerUtils;

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
                if (context instanceof MainActivity)
                    ((MainActivity) context).receiveUpdateBroadcast(title, artist, isPlaying);
                else if (context instanceof PlaylistActivity)
                    ((PlaylistActivity) context).receiveUpdateBroadcast(title, artist, isPlaying);
                break;
        }
    }
}
