package com.devenik7.android.genericmusicplayer;

import android.app.LoaderManager;
import android.app.Service;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import com.devenik7.android.genericmusicplayer.PlayerContract.MusicEntry;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by nisha on 06-Aug-17.
 */

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final int GLOBAL_MUSIC_LOADER = 1;
    MediaPlayer player;
    Cursor playList;
    Uri currentPlayListUri;
    int currentTrackPostiton;
    int currentTrackID;
    String currentTrackTitle;
    String currentTrackArtist;
    

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        playList = null;
        currentTrackPostiton = 0;
        currentTrackID = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(MusicPlayerUtils.START_ACTION)) {
            Uri uri = intent.getParcelableExtra(MusicPlayerUtils.CONTENT_URI);
            int id = intent.getIntExtra(PlayerContract.MusicEntry.MUSIC_ID, 0);
            int position = intent.getIntExtra(MusicPlayerUtils.POSITION_IN_LIST, 0);
            playMusicWithDetails(uri, id, position);
        }

        return START_NOT_STICKY;
    }

    private void playMusicWithDetails(Uri uri, int id, int position) {
        if (uri.equals(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)) {
            currentTrackPostiton = position;
            currentTrackID = id;
            if (playList != null && currentPlayListUri == uri) {
                playList.moveToPosition(currentTrackPostiton);
                startCurrentMusic();
            }
            else {
                playList = getContentResolver().query(uri, MusicPlayerUtils.EXTERNAL_MUSIC_PROJECTION, MusicPlayerUtils.EXTERNAL_MUSIC_SELECTION, null, MusicPlayerUtils.EXTERNAL_MUSIC_SORT_ORDER);
                playList.moveToPosition(currentTrackPostiton);
                startCurrentMusic();
            }
        }
    }

    private void startCurrentMusic() {
        currentTrackTitle = playList.getString(playList.getColumnIndex(MusicEntry.MUSIC_TITLE));
        currentTrackArtist = playList.getString(playList.getColumnIndex(MusicEntry.MUSIC_ARTIST));
        String path = playList.getString(playList.getColumnIndex(MusicEntry.MUSIC_PATH));
        try {
            player.reset();
            player.setDataSource(path);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        if (playList != null) {
            if (!playList.moveToNext()) {
                playList.moveToPosition(0);
            }
            currentTrackPostiton = playList.getPosition();
            currentTrackID = playList.getInt(playList.getColumnIndex(MusicEntry.MUSIC_ID));
            startCurrentMusic();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.start();
        startForeground(1, NotificationUtils.playNotification(this, currentTrackTitle, currentTrackArtist));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        playList.close();
    }
}
