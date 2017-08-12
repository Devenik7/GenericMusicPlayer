package com.devenik7.android.genericmusicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by nisha on 12-Aug-17.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new PlaylistsFragment();
            case 1: return new GlobalMusicFragment();
            default: return new GlobalMusicFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "PLAYLISTS";
            case 1: return "ALL MUSIC";
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
