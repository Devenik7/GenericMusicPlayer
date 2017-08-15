package com.devenik7.android.genericmusicplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import com.devenik7.android.genericmusicplayer.PlayerContract.MusicEntry;

/**
 * Created by nisha on 14-Aug-17.
 */

class PlaylistContentLoader extends AsyncTaskLoader<Cursor> {

    private String playlistName;

    PlaylistContentLoader(Context context, String name) {
        super(context);

        playlistName = name;
    }

    @Override
    public Cursor loadInBackground() {
        MusicDbHelper helper = new MusicDbHelper(getContext());
        SQLiteDatabase database = helper.getReadableDatabase();

        String[] projection = {
                MusicEntry.MUSIC_ID,
                MusicEntry.MUSIC_TITLE,
                MusicEntry.MUSIC_ARTIST,
                MusicEntry.MUSIC_ALBUM,
                MusicEntry.MUSIC_PATH,
                MusicEntry.MUSIC_DURATION,
        };

        String selection = PlayerContract.PlaylistEntry.PLAYLIST_NAME + " = ?";

        String[] selectionArgs = {
            playlistName
        };

        String sortOrder = PlayerContract.MusicInPlaylistEntry.TIME_ADDED + " DESC";

        return database.query(PlayerContract.MusicInPlaylistEntry.TABLE_NAME,projection, selection, selectionArgs, null, null, sortOrder);
    }
}
