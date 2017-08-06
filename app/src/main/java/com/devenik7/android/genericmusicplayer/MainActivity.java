package com.devenik7.android.genericmusicplayer;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.security.Permission;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ListView allMusicListView;
    final int GLOBAL_MUSIC_LOADER = 1;
    final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 11;
    Cursor globalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allMusicListView = (ListView) findViewById(R.id.all_music_list_view);
        allMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startMusic(i);
            }
        });

        if (checkForPermission()) {
            getAllMusicContent();
            checkAndStoreAllMusicContent();
        }

    }

    private void startMusic(int position) {
        globalList.moveToPosition(position);
        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.setAction(MusicPlayerUtils.START_ACTION);
        intent.putExtra(MusicPlayerUtils.CONTENT_URI, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(PlayerContract.MusicEntry.MUSIC_ID, globalList.getInt(globalList.getColumnIndex(PlayerContract.MusicEntry.MUSIC_ID)));
        intent.putExtra(MusicPlayerUtils.POSITION_IN_LIST, position);
        startService(intent);
    }

    private void getAllMusicContent() {
        getLoaderManager().initLoader(GLOBAL_MUSIC_LOADER, null, this).forceLoad();
    }

    private void checkAndStoreAllMusicContent() {

    }

    private boolean checkForPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case GLOBAL_MUSIC_LOADER:
                return new GlobalMusicLoader(getApplicationContext());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader instanceof GlobalMusicLoader) {
            globalList = cursor;
            allMusicListView.setAdapter(new SimpleMusicAdapter(getApplicationContext(), globalList));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //allMusicListView.setAdapter(new SimpleMusicAdapter(getApplicationContext(), null));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getAllMusicContent();
            }

        }
    }
}
