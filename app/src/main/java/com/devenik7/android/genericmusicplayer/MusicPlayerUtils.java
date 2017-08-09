package com.devenik7.android.genericmusicplayer;

import android.provider.MediaStore;

/**
 * Created by nisha on 06-Aug-17.
 */

public class MusicPlayerUtils {

    public static final String START_ACTION = "start";
    public static final String PLAY_ACTION = "play";
    public static final String PAUSE_ACTION = "pause";
    public static final String NEXT_ACTION = "next";
    public static final String PREVIOUS_ACTION = "previous";
    public static final String POSITION_IN_LIST = "position";
    public static final String CONTENT_URI = "uri";
    public static final String UPDATE_INFO_BROADCAST = "com.devenik7.android.genericmusicplayer.update_info";
    public static final String INFO_REQUEST = "infoRequest";
    public static final String IS_PLAYING = "isPlaying";

    public static final String[] EXTERNAL_MUSIC_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
    };

    public static final String EXTERNAL_MUSIC_SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0";

    public static final String EXTERNAL_MUSIC_SORT_ORDER = MediaStore.Audio.Media.ARTIST + " DESC";
}
