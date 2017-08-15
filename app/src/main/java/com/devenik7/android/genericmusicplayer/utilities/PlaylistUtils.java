package com.devenik7.android.genericmusicplayer.utilities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.devenik7.android.genericmusicplayer.MusicDbHelper;
import com.devenik7.android.genericmusicplayer.PlayerContract.PlaylistEntry;

/**
 * Created by nisha on 11-Aug-17.
 */

public class PlaylistUtils {

    public static final String[] PLAYLIST_PROJECTION = {
            PlaylistEntry.PLAYLIST_ID,
            PlaylistEntry.PLAYLIST_NAME,
            PlaylistEntry.PLAYLIST_DESCRIPTION
    };

    public static long addPlayList (MusicDbHelper helper, String title) {
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlaylistEntry.PLAYLIST_NAME, title);
        values.put(PlaylistEntry.PLAYLIST_DESCRIPTION, "none");
        return database.insert(PlaylistEntry.TABLE_NAME, null, values);
    }

    public static long addPlayList (MusicDbHelper helper, String title, String description) {
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlaylistEntry.PLAYLIST_NAME, title);
        values.put(PlaylistEntry.PLAYLIST_DESCRIPTION, description);
        return database.insert(PlaylistEntry.TABLE_NAME, null, values);
    }

}
