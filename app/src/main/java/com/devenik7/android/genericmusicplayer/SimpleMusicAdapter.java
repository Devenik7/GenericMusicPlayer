package com.devenik7.android.genericmusicplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by nisha on 06-Aug-17.
 */

public class SimpleMusicAdapter extends CursorAdapter {

    public SimpleMusicAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.music_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)).trim();
        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)).trim();
        int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        String durationMinSecFormat = (int) Math.floor((duration/1000) / 60) + ":" + (int) Math.floor(duration/1000) % 60;

        ((TextView) view.findViewById(R.id.music_title_text_view)).setText(title);
        ((TextView) view.findViewById(R.id.music_artist_text_view)).setText(artist);
        ((TextView) view.findViewById(R.id.music_duration_text_view)).setText((durationMinSecFormat));
    }
}
