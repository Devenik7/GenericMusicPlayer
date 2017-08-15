package com.devenik7.android.genericmusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by nisha on 15-Aug-17.
 */

public class testing extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing);

        Intent intent = getIntent();
        ArrayList<Music> list = intent.getParcelableArrayListExtra("MusicList");

        Toast.makeText(this, list.toString(), Toast.LENGTH_SHORT).show();
    }
}
