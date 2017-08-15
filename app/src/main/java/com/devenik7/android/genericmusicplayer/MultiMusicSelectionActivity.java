package com.devenik7.android.genericmusicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.devenik7.android.genericmusicplayer.PlayerContract.MusicEntry;

import java.util.HashMap;

/**
 * Created by nisha on 14-Aug-17.
 */

public class MultiMusicSelectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GLOBAL_MUSIC_LOADER = 123;

    ListView globalMusicSelectionView;
    Cursor cursor;
    HashMap<Integer, Integer> list;
    String playlistName;
    SQLiteDatabase database;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_music_selection_activity);

        playlistName = getIntent().getStringExtra(PlayerContract.PlaylistEntry.PLAYLIST_NAME);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Select Music");
        bar.setDisplayHomeAsUpEnabled(true);

        database = (new MusicDbHelper(this)).getWritableDatabase();

        globalMusicSelectionView = (ListView) findViewById(R.id.global_music_list_view);
        globalMusicSelectionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (list.get((Integer) v.getTag()) == null) {
                    list.put((Integer) v.getTag(), 1);
                    v.findViewById(R.id.is_selected).setSelected(true);
                } else if (list.get((Integer) v.getTag()) == 1) {
                    list.put((Integer) v.getTag(), 0);
                    v.findViewById(R.id.is_selected).setSelected(false);
                } else {
                    list.put((Integer) v.getTag(), 1);
                    v.findViewById(R.id.is_selected).setSelected(true);
                }
            }
        });
        findViewById(R.id.action_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
        list = new HashMap<>();

        getSupportLoaderManager().initLoader(GLOBAL_MUSIC_LOADER, null, this).forceLoad();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("")
                .setMessage("Finalize Selection ?")
                .setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addSelectionToPlaylist();
                    }
                })
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void addSelectionToPlaylist() {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

        cursor.moveToFirst();
        do {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            if (list.get(id) != null && list.get(id).equals(1)) {
                insertMusicIntoPlaylist();
            }
        } while (cursor.moveToNext());

        setResult(RESULT_OK, null);
        finish();
    }

    private void insertMusicIntoPlaylist() {
        ContentValues values = new ContentValues();
        values.put(PlayerContract.PlaylistEntry.PLAYLIST_NAME, playlistName);
        values.put(MusicEntry._ID, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        values.put(MusicEntry.MUSIC_TITLE, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        values.put(MusicEntry.MUSIC_ARTIST, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        values.put(MusicEntry.MUSIC_PATH, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        values.put(MusicEntry.MUSIC_DURATION, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        values.put(PlayerContract.MusicInPlaylistEntry.TIME_ADDED, System.currentTimeMillis());

        database.insert(PlayerContract.MusicInPlaylistEntry.TABLE_NAME, null, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case GLOBAL_MUSIC_LOADER:
                return new GlobalMusicLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
        globalMusicSelectionView.setAdapter(new CursorAdapter(getApplicationContext(), data, false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(getApplicationContext()).inflate(R.layout.music_item_selectable, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)).trim();
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)).trim();
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String durationMinSecFormat = (int) Math.floor((duration / 1000) / 60) + ":" + (int) Math.floor(duration / 1000) % 60;

                ((TextView) view.findViewById(R.id.music_title_text_view)).setText(title);
                ((TextView) view.findViewById(R.id.music_artist_text_view)).setText(artist);
                ((TextView) view.findViewById(R.id.music_duration_text_view)).setText((durationMinSecFormat));

                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                view.setTag(id);

                if (list.get(id) == null) {
                    view.findViewById(R.id.is_selected).setSelected(false);
                } else {
                    view.findViewById(R.id.is_selected).setSelected(list.get(id) == 1);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
