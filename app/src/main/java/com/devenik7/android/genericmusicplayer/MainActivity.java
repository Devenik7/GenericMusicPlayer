package com.devenik7.android.genericmusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 11;

    ViewPager mainViewPager;
    PagerTabStrip mainPagerTabStrip;
    View playerStatusView;
    TextView titleView;
    TextView artistView;
    ImageView isPlayingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        if (checkForPermission()) {
            mainViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        }

        registerPlayerBroadcastReceiver();
        requestInfoFromPlayerService();
    }

    private void setupUI() {
        mainViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mainPagerTabStrip = (PagerTabStrip) findViewById(R.id.main_pager_tab_strip);
        mainPagerTabStrip.setTextColor(0xffffffff);
        mainPagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mainPagerTabStrip.setTabIndicatorColor(0xffffffff);

        playerStatusView = findViewById(R.id.player_status_view);
        titleView = (TextView) playerStatusView.findViewById(R.id.player_status_title_view);
        artistView = (TextView) playerStatusView.findViewById(R.id.player_status_artist_view);
        isPlayingView = (ImageView) playerStatusView.findViewById(R.id.player_status_image_view);
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
        IntentFilter filter = new IntentFilter(MusicPlayerUtils.UPDATE_INFO_BROADCAST);
        registerReceiver(new PlayerBroadcastReceiver(), filter);
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

    public void pauseMusic(View view) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mainViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
                }
            }
            break;
        }
    }

}
