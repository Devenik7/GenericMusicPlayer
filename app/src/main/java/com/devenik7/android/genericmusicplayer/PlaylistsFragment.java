package com.devenik7.android.genericmusicplayer;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by nisha on 12-Aug-17.
 */

public class PlaylistsFragment extends Fragment {

    ListView playlistsView;
    FloatingActionButton addPlayListButton;
    MusicDbHelper helper;
    CursorAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playlists_fragment, container, false);
        playlistsView = (ListView) rootView.findViewById(R.id.playlists_list_view);
        addPlayListButton = (FloatingActionButton) rootView.findViewById(R.id.add_playlist_button);
        addPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePlaylistDialog();
            }
        });
        helper = new MusicDbHelper(getActivity());
        adapter = new CursorAdapter(getActivity(), null, false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView) view.findViewById(R.id.playlist_name_view)).setText(cursor.getString(cursor.getColumnIndex(PlayerContract.PlaylistEntry.PLAYLIST_NAME)));
            }
        };
        playlistsView.setAdapter(adapter);
        setPlaylistContent();

        return rootView;
    }

    private void setPlaylistContent() {
        AsyncTask task = new AsyncTask() {

            @Nullable
            @Override
            protected Cursor doInBackground(Object[] params) {
                return helper.getReadableDatabase().query(PlayerContract.PlaylistEntry.TABLE_NAME, PlaylistUtils.PLAYLIST_PROJECTION, null, null, null, null, null);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                adapter.changeCursor((Cursor) o);
                adapter.notifyDataSetChanged();
            }
        };
        task.execute();
    }

    public void showCreatePlaylistDialog() {
        CreatePlaylistDialogFragment dialogFragment = new CreatePlaylistDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), "create_playlist_dialog");
    }

}
