package com.devenik7.android.genericmusicplayer.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by nisha on 06-Aug-17.
 */

public class GlobalMusicLoader extends android.support.v4.content.AsyncTaskLoader<Cursor> {

    public GlobalMusicLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String sortOrder = MediaStore.Audio.Media.ARTIST + " DESC";

        return getContext().getContentResolver().query(uri, projection, selection, null, sortOrder);
    }

}
