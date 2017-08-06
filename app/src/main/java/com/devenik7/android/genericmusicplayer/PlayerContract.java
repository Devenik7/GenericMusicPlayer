package com.devenik7.android.genericmusicplayer;

import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * Created by nisha on 05-Aug-17.
 */

class PlayerContract {

    private PlayerContract() {}

    static class MusicEntry implements BaseColumns {

        final static String TABLE_NAME = "main_contents";
        final static String MUSIC_ID = MediaStore.Audio.Media._ID;
        final static String MUSIC_TITLE = MediaStore.Audio.Media.TITLE;
        final static String MUSIC_DISPLAY_NAME = MediaStore.Audio.Media.DISPLAY_NAME;
        final static String MUSIC_ARTIST = MediaStore.Audio.Media.ARTIST;
        final static String MUSIC_PATH = MediaStore.Audio.Media.DATA;
        final static String MUSIC_DURATION = MediaStore.Audio.Media.DURATION;
        final static String MUSIC_FREQUENCY = "frequency";
        final static String MUSIC_RATING = "rating";

        public final static int MUSIC_GOOD = 1;
        public final static int MUSIC_BAD = -1;
        public final static int MUSIC_NEUTRAL = 0;

    }
}
