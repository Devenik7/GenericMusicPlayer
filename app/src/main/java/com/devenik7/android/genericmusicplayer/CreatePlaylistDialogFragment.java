package com.devenik7.android.genericmusicplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.devenik7.android.genericmusicplayer.activities.MainActivity;

/**
 * Created by nisha on 13-Aug-17.
 */

public class CreatePlaylistDialogFragment extends DialogFragment {
    public CreatePlaylistDialogFragment() {
        super();
    }

    EditText titleView;
    EditText descriptionView;
    Toast toast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.create_playlist_dialog_fragment, container, false);
        titleView = (EditText) rootView.findViewById(R.id.title_view);
        descriptionView = (EditText) rootView.findViewById(R.id.description_view);
        rootView.findViewById(R.id.action_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDone();
            }
        });
        rootView.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    void actionDone () {
        if (titleView.getText().toString().trim().equals("")) {
            if(toast == null)
                toast = Toast.makeText(getActivity(), "Title can't be empty !", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            ((MainActivity)getActivity()).createNewPlaylist(titleView.getText().toString().trim(), descriptionView.getText().toString().trim());
            getDialog().dismiss();
        }
    }
}
