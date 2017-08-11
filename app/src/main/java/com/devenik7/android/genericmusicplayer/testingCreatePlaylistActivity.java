package com.devenik7.android.genericmusicplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by nisha on 11-Aug-17.
 */

public class testingCreatePlaylistActivity extends AppCompatActivity {

    ListView playLists;
    MusicDbHelper helper;
    CursorAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing);

        playLists = (ListView) findViewById(R.id.testing_list_view);
        helper = new MusicDbHelper(this);
        adapter = new CursorAdapter(this, null, false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView) view.findViewById(R.id.playlist_name_view)).setText(cursor.getString(cursor.getColumnIndex(PlayerContract.PlaylistEntry.PLAYLIST_NAME)));
            }
        };
        playLists.setAdapter(adapter);
        setPlaylistContent();
    }

    private void setPlaylistContent() {
        Cursor cursor = helper.getReadableDatabase().query(PlayerContract.PlaylistEntry.TABLE_NAME, PlaylistUtils.PLAYLIST_PROJECTION, null, null, null, null, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    public void addNewPlaylist(View view) {
        PlaylistUtils.addPlayList(helper, "something");
        setPlaylistContent();
    }

}
