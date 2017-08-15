package com.devenik7.android.genericmusicplayer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devenik7.android.genericmusicplayer.PlayerContract.MusicEntry;

import java.util.ArrayList;

/**
 * Created by nisha on 14-Aug-17.
 */

public class PlaylistActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PLAYLIST_CONTENT_LOADER = 111;
    private static final int SONG_SELECTION_INTENT = 1234;

    String playlistName;
    ListView playlistContentView;

    View playerStatusView;
    TextView titleView;
    TextView artistView;
    ImageView isPlayingView;
    PlayerBroadcastReceiver playerBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        Intent intent = getIntent();
        playlistName = intent.getStringExtra(PlayerContract.PlaylistEntry.PLAYLIST_NAME);

        setupUI();

        registerPlayerBroadcastReceiver();
        requestInfoFromPlayerService();

        getSupportLoaderManager().initLoader(PLAYLIST_CONTENT_LOADER, null, this).forceLoad();
    }

    private void setupUI() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle(playlistName);
        bar.setDisplayHomeAsUpEnabled(true);

        playlistContentView = (ListView) findViewById(R.id.playlist_content_view);
        playlistContentView.setEmptyView(findViewById(R.id.empty_view));
        playlistContentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMusic(position);
            }
        });
        findViewById(R.id.add_music_to_playlist_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusicSelectionActivity();
            }
        });

        playerStatusView = findViewById(R.id.player_status_view);
        titleView = (TextView) playerStatusView.findViewById(R.id.player_status_title_view);
        artistView = (TextView) playerStatusView.findViewById(R.id.player_status_artist_view);
        isPlayingView = (ImageView) playerStatusView.findViewById(R.id.player_status_image_view);
        isPlayingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseOrPlayMusic(v);
            }
        });
    }

    private void startMusic(int position) {
        ArrayList<Music> playlist = new ArrayList<>();
        Cursor cursor = ((CursorAdapter) playlistContentView.getAdapter()).getCursor();
        cursor.moveToFirst();
        do {
            playlist.add(new Music(
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            ));
        } while (cursor.moveToNext());
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.START_PLAYLIST);
        intent.putParcelableArrayListExtra("playlist", playlist);
        intent.putExtra(MusicPlayerUtils.POSITION_IN_LIST, position);
        startService(intent);
    }

    private void showMusicSelectionActivity() {
        Intent intent = new Intent(this, MultiMusicSelectionActivity.class);
        intent.putExtra(PlayerContract.PlaylistEntry.PLAYLIST_NAME, playlistName);
        startActivityForResult(intent, SONG_SELECTION_INTENT);
    }

    private void registerPlayerBroadcastReceiver() {
        if (playerBroadcastReceiver == null)
            playerBroadcastReceiver = new PlayerBroadcastReceiver();
        IntentFilter filter = new IntentFilter(MusicPlayerUtils.UPDATE_INFO_BROADCAST);
        registerReceiver(playerBroadcastReceiver, filter);
    }

    private void requestInfoFromPlayerService() {
        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.INFO_REQUEST);
        startService(intent);
    }

    public void receiveUpdateBroadcast(String title, String artist, boolean isPlaying) {
        if (title != null) {
            // the GONE and VISIBLE are for an animating effect
            if (!titleView.getText().equals(title.trim()) || !artistView.getText().equals(artist.trim())) { // this condition so that it doesn't animate when pressing the button beside them
                titleView.setVisibility(View.GONE);
                artistView.setVisibility(View.GONE);
                titleView.setText(title.trim());
                titleView.setVisibility(View.VISIBLE);
                if (artist != null) {
                    artistView.setText(artist.trim());
                    artistView.setVisibility(View.VISIBLE);
                }
            }

            if (isPlaying)
                isPlayingView.setImageResource(android.R.drawable.ic_media_pause);
            else
                isPlayingView.setImageResource(android.R.drawable.ic_media_play);
            isPlayingView.setTag(isPlaying);
        } else {
            titleView.setText("Select a Song");
            artistView.setVisibility(View.GONE);
            isPlayingView.setTag(null);
        }
    }

    public void pauseOrPlayMusic(View view) {
        Object tag = view.getTag();
        if (tag != null) {
            Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
            if ((boolean) tag)
                intent.setAction(MusicPlayerUtils.PAUSE_ACTION);
            else
                intent.setAction(MusicPlayerUtils.PLAY_ACTION);
            startService(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PLAYLIST_CONTENT_LOADER:
                return new PlaylistContentLoader(getApplicationContext(), playlistName);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        playlistContentView.setAdapter(new CursorAdapter(getApplicationContext(), data, false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String title = cursor.getString(cursor.getColumnIndex(MusicEntry.MUSIC_TITLE)).trim();
                String artist = cursor.getString(cursor.getColumnIndex(MusicEntry.MUSIC_ARTIST)).trim();
                int duration = cursor.getInt(cursor.getColumnIndex(MusicEntry.MUSIC_DURATION));
                String durationMinSecFormat = (int) Math.floor((duration / 1000) / 60) + ":" + (int) Math.floor(duration / 1000) % 60;

                ((TextView) view.findViewById(R.id.music_title_text_view)).setText(title);
                ((TextView) view.findViewById(R.id.music_artist_text_view)).setText(artist);
                ((TextView) view.findViewById(R.id.music_duration_text_view)).setText((durationMinSecFormat));
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        playlistContentView.setAdapter(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SONG_SELECTION_INTENT: {
                if (resultCode == RESULT_OK)
                    getSupportLoaderManager().initLoader(PLAYLIST_CONTENT_LOADER, null, this).forceLoad();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerPlayerBroadcastReceiver();
        requestInfoFromPlayerService();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(playerBroadcastReceiver);
    }
}
