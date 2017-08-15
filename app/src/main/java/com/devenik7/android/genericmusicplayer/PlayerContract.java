package com.devenik7.android.genericmusicplayer;

import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * Created by nisha on 05-Aug-17.
 */

public class PlayerContract {

    private PlayerContract() {}

    public static class MusicEntry implements BaseColumns {

        public final static String TABLE_NAME = "main_contents";
        public final static String MUSIC_ID = MediaStore.Audio.Media._ID;
        public final static String MUSIC_TITLE = MediaStore.Audio.Media.TITLE;
        public final static String MUSIC_ARTIST = MediaStore.Audio.Media.ARTIST;
        public final static String MUSIC_ALBUM = MediaStore.Audio.Media.ALBUM;
        public final static String MUSIC_PATH = MediaStore.Audio.Media.DATA;
        public final static String MUSIC_DURATION = MediaStore.Audio.Media.DURATION;
        public final static String MUSIC_FREQUENCY = "frequency";
        public final static String MUSIC_RATING = "rating";

        public final static int MUSIC_GOOD = 1;
        public final static int MUSIC_BAD = -1;
        public final static int MUSIC_NEUTRAL = 0;

    }

    public static class PlaylistEntry implements BaseColumns {

        public final static String TABLE_NAME = "playlists";
        public final static String PLAYLIST_ID = BaseColumns._ID;
        public final static String PLAYLIST_NAME = "playlist_name";
        public final static String PLAYLIST_DESCRIPTION = "playlist_description";

    }

    public static class MusicInPlaylistEntry implements BaseColumns {

        public final static String TABLE_NAME = "music_in_playlists";
        public final static String TIME_ADDED = "time_added";

    }
}
