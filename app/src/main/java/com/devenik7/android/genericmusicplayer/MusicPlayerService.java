package com.devenik7.android.genericmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.devenik7.android.genericmusicplayer.PlayerContract.MusicEntry;

import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nisha on 06-Aug-17.
 */

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {

    AudioManager manager;
    MediaPlayer player;
    ArrayList<Music> playList1;
    int currentTrackPosition;
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
        playList1 = new ArrayList<>();
        currentTrackPosition = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case MusicPlayerUtils.START_PLAYLIST:
                playList1 = intent.getParcelableArrayListExtra("playlist");
                int position1 = intent.getIntExtra(MusicPlayerUtils.POSITION_IN_LIST, 0);
                startPlaylistAtPosition(position1);
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

    private void startPlaylistAtPosition (int position) {
        currentTrackPosition = position;
        if (playList1 != null && playList1.size() > 0) {
            startCurrentMusic();
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
        if (playList1 == null || playList1.size() < 1) return;
        currentTrackPosition = (currentTrackPosition-1 < 0)? playList1.size()-1: currentTrackPosition-1;
        startCurrentMusic();
    }

    private void playNextTrack() {
        onCompletion(player);
    }

    private void startCurrentMusic() {
        currentTrackTitle = playList1.get(currentTrackPosition).getTitle();
        currentTrackArtist = playList1.get(currentTrackPosition).getArtist();
        String path = playList1.get(currentTrackPosition).getDataPath();
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
        if (playList1 != null && playList1.size() > 0) {
            currentTrackPosition = (currentTrackPosition+1 >= playList1.size())? 0: currentTrackPosition+1;
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
