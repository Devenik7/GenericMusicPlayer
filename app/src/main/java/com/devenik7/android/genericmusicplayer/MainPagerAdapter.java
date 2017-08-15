package com.devenik7.android.genericmusicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.devenik7.android.genericmusicplayer.main_fragments.AlbumsFragment;
import com.devenik7.android.genericmusicplayer.main_fragments.ArtistsFragment;
import com.devenik7.android.genericmusicplayer.main_fragments.GlobalMusicFragment;
import com.devenik7.android.genericmusicplayer.main_fragments.PlaylistsFragment;

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
            case 2: return new ArtistsFragment();
            case 3: return new AlbumsFragment();
            default: return new GlobalMusicFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "PLAYLISTS";
            case 1: return "ALL MUSIC";
            case 2: return "ARTISTS";
            case 3: return "ALBUMS";
            default: return "ALL MUSIC";
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
