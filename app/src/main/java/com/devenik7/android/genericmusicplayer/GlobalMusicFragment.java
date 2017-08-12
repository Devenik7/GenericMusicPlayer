package com.devenik7.android.genericmusicplayer;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by nisha on 12-Aug-17.
 */

public class GlobalMusicFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final int GLOBAL_MUSIC_LOADER = 1;

    ListView globalMusicListView;

    Cursor globalList;

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
        globalList.moveToPosition(position);
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.START_ACTION);
        intent.putExtra(MusicPlayerUtils.CONTENT_URI, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(PlayerContract.MusicEntry.MUSIC_ID, globalList.getInt(globalList.getColumnIndex(PlayerContract.MusicEntry.MUSIC_ID)));
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
            globalList = data;
            globalMusicListView.setAdapter(new SimpleMusicAdapter(getActivity(), globalList));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        globalMusicListView.setAdapter(new SimpleMusicAdapter(getActivity(), null));
    }
}
