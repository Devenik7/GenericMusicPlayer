package com.devenik7.android.genericmusicplayer.main_fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.devenik7.android.genericmusicplayer.loaders.GlobalMusicLoader;
import com.devenik7.android.genericmusicplayer.activities.MainActivity;
import com.devenik7.android.genericmusicplayer.Music;
import com.devenik7.android.genericmusicplayer.MusicPlayerService;
import com.devenik7.android.genericmusicplayer.R;
import com.devenik7.android.genericmusicplayer.utilities.MusicPlayerUtils;

import java.util.ArrayList;

/**
 * Created by nisha on 12-Aug-17.
 */

public class GlobalMusicFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final int GLOBAL_MUSIC_LOADER = 1;

    ListView globalMusicListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.global_music_fragment, container, false);
        globalMusicListView = (ListView) rootView.findViewById(R.id.global_music_list_view);
        globalMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startMusic(i);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkForPermission()) {
            getAllMusicContent();
            checkAndStoreAllMusicContent();
        }
    }

    private void startMusic(int position) {
        ArrayList<Music> playlist = new ArrayList<>();
        Cursor globalList = ((CursorAdapter) globalMusicListView.getAdapter()).getCursor();
        globalList.moveToFirst();
        do {
            playlist.add(new Music(
                    globalList.getInt(globalList.getColumnIndex(MediaStore.Audio.Media._ID)),
                    globalList.getString(globalList.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    globalList.getString(globalList.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    globalList.getString(globalList.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    globalList.getString(globalList.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    globalList.getInt(globalList.getColumnIndex(MediaStore.Audio.Media.DURATION))
            ));
        } while (globalList.moveToNext());
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.START_PLAYLIST);
        intent.putParcelableArrayListExtra("playlist", playlist);
        intent.putExtra(MusicPlayerUtils.POSITION_IN_LIST, position);
        getActivity().startService(intent);
    }

    private boolean checkForPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ((MainActivity) getActivity()).checkForPermission();
            return false;
        }
    }

    private void getAllMusicContent() {
        getLoaderManager().initLoader(GLOBAL_MUSIC_LOADER, null, this).forceLoad();
    }

    private void checkAndStoreAllMusicContent() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case GLOBAL_MUSIC_LOADER:
                return new GlobalMusicLoader(getActivity());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader instanceof GlobalMusicLoader) {
            globalMusicListView.setAdapter(new CursorAdapter(getActivity(), data, false) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    return LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
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
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        globalMusicListView.setAdapter(null);
    }

}
