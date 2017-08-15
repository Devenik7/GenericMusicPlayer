package com.devenik7.android.genericmusicplayer.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.devenik7.android.genericmusicplayer.broadcast_receivers.PlayerBroadcastReceiver;
import com.devenik7.android.genericmusicplayer.MainPagerAdapter;
import com.devenik7.android.genericmusicplayer.MusicDbHelper;
import com.devenik7.android.genericmusicplayer.MusicPlayerService;
import com.devenik7.android.genericmusicplayer.R;
import com.devenik7.android.genericmusicplayer.utilities.MusicPlayerUtils;
import com.devenik7.android.genericmusicplayer.utilities.PlaylistUtils;

public class MainActivity extends AppCompatActivity {

    final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 11;

    ViewPager mainViewPager;
    PagerSlidingTabStrip strip;

    MusicDbHelper helper;

    View playerStatusView;
    TextView titleView;
    TextView artistView;
    ImageView isPlayingView;
    PlayerBroadcastReceiver playerBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new MusicDbHelper(this);

        setupUI();

        if (checkForPermission()) {
            setupPagerAndStrip();
        }

        registerPlayerBroadcastReceiver();
        requestInfoFromPlayerService();
    }

    private void setupUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mainViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        strip = (PagerSlidingTabStrip) findViewById(R.id.main_pager_strip);

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

    private void setupPagerAndStrip() {
        mainViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        strip.setViewPager(mainViewPager);
    }

    public boolean checkForPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            return false;
        }
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

    public void createNewPlaylist(String title, String description) {
        if (description.equals("")) {
            PlaylistUtils.addPlayList(helper, title);
        } else {
            PlaylistUtils.addPlayList(helper, title, description);
        }
        setupPagerAndStrip();
        mainViewPager.setCurrentItem(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupPagerAndStrip();
                }
            }
            break;
        }
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
