package com.devenik7.android.genericmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import com.devenik7.android.genericmusicplayer.PlayerContract.MusicEntry;

import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by nisha on 06-Aug-17.
 */

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {

    AudioManager manager;
    MediaPlayer player;
    Cursor playList;
    Uri currentPlayListUri;
    int currentTrackPosition;
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

        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        playList = null;
        currentTrackPosition = 0;
        currentTrackID = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case MusicPlayerUtils.START_ACTION:
                Uri uri = intent.getParcelableExtra(MusicPlayerUtils.CONTENT_URI);
                int id = intent.getIntExtra(MusicEntry.MUSIC_ID, 0);
                int position = intent.getIntExtra(MusicPlayerUtils.POSITION_IN_LIST, 0);
                playMusicWithDetails(uri, id, position);
                break;
            case MusicPlayerUtils.PLAY_ACTION:
                resumeCurrentTrack();
                break;
            case MusicPlayerUtils.PAUSE_ACTION:
                pauseCurrentTrack();
                break;
            case MusicPlayerUtils.PREVIOUS_ACTION:
                playPreviousTrack();
                break;
            case MusicPlayerUtils.NEXT_ACTION:
                playNextTrack();
                break;
            case NotificationUtils.NOTIFICATION_SWIPED_ACTION:
                closePlayer();
                break;
            case MusicPlayerUtils.INFO_REQUEST:
                sendUpdateBroadcast();
                break;
        }

        return START_NOT_STICKY;
    }

    private void playMusicWithDetails(Uri uri, int id, int position) {
        if (uri.equals(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)) {
            currentTrackPosition = position;
            currentTrackID = id;
            if (playList != null && currentPlayListUri == uri) {
                playList.moveToPosition(currentTrackPosition);
                startCurrentMusic();
            } else {
                playList = getContentResolver().query(uri, MusicPlayerUtils.EXTERNAL_MUSIC_PROJECTION, MusicPlayerUtils.EXTERNAL_MUSIC_SELECTION, null, MusicPlayerUtils.EXTERNAL_MUSIC_SORT_ORDER);
                playList.moveToPosition(currentTrackPosition);
                startCurrentMusic();
            }
        }
    }

    private void pauseCurrentTrack() {
        manager.abandonAudioFocus(this);
        if (player != null && player.isPlaying()) {
            player.pause();
            stopForeground(false);
            NotificationUtils.showPausedNotification(this, currentTrackTitle, currentTrackArtist);
            sendUpdateBroadcast();
        }
    }

    private void resumeCurrentTrack() {
        if (player != null) {
            onPrepared(player);
        }
    }

    private void playPreviousTrack() {
        if (playList == null || playList.getCount() < 1) return;
        if (player == null) player = new MediaPlayer();
        if (playList.getPosition() == 0)
            playList.moveToLast();
        else
            playList.moveToPrevious();
        startCurrentMusic();
    }

    private void playNextTrack() {
        if (playList == null || playList.getCount() < 1) return;
        if (player == null) player = new MediaPlayer();
        if (!playList.moveToNext()) {
            playList.moveToPosition(0);
        }
        startCurrentMusic();
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

    private void closePlayer() {
        onDestroy();
    }

    private void sendUpdateBroadcast() {
        // need to be called only in possible states due to usage of player properties
        Intent intent = new Intent();
        intent.setAction(MusicPlayerUtils.UPDATE_INFO_BROADCAST);
        intent.putExtra(MusicEntry.MUSIC_TITLE, currentTrackTitle);
        intent.putExtra(MusicEntry.MUSIC_ARTIST, currentTrackArtist);
        if (currentTrackTitle != null) intent.putExtra(MusicPlayerUtils.IS_PLAYING, player.isPlaying());
        sendBroadcast(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        if (playList != null) {
            if (!playList.moveToNext()) {
                playList.moveToPosition(0);
            }
            currentTrackPosition = playList.getPosition();
            currentTrackID = playList.getInt(playList.getColumnIndex(MusicEntry.MUSIC_ID));
            startCurrentMusic();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_GAIN) {
            player.start();
            startForeground(NotificationUtils.FOREGROUND_NOTIFICATION_ID, NotificationUtils.playNotification(this, currentTrackTitle, currentTrackArtist));
            sendUpdateBroadcast();
        } else {
            Toast.makeText(this, "Can't play Music right now !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        currentTrackTitle = null;
        sendUpdateBroadcast();
        manager.abandonAudioFocus(this);
        playList.close();
        player.release();
        stopSelf();
    }

    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pauseCurrentTrack();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                player.setVolume(0.25f, 0.25f);
                //pauseCurrentTrack();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                player.setVolume(1f, 1f);
                resumeCurrentTrack();
                break;
        }
    }
}
