package com.devenik7.android.genericmusicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.devenik7.android.genericmusicplayer.PlayerContract.MusicEntry;
import com.devenik7.android.genericmusicplayer.PlayerContract.PlaylistEntry;
import com.devenik7.android.genericmusicplayer.PlayerContract.MusicInPlaylistEntry;

/**
 * Created by nisha on 05-Aug-17.
 */

public class MusicDbHelper extends SQLiteOpenHelper {

    public final static String LOG_TAG = MusicDbHelper.class.getSimpleName();

    private final static String DATABASE_NAME = "genericmusicplayer.db";

    private final static int DATABASE_VERSION = 1;

    public MusicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_MUSIC_TABLE = "CREATE TABLE " + MusicEntry.TABLE_NAME + " ("
                + MusicEntry.MUSIC_ID + " INTEGER NOT NULL, "
                + MusicEntry.MUSIC_TITLE + " TEXT NOT NULL, "
                + MusicEntry.MUSIC_DISPLAY_NAME + " TEXT NOT NULL, "
                + MusicEntry.MUSIC_PATH + " TEXT NOT NULL, "
                + MusicEntry.MUSIC_DURATION + " INTEGER NOT NULL, "
                + MusicEntry.MUSIC_ARTIST + " TEXT, "
                + MusicEntry.MUSIC_FREQUENCY + " INTEGER DEFAULT 0, "
                + MusicEntry.MUSIC_RATING + " INTEGER DEFAULT 0"
                + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_MUSIC_TABLE);

        String SQL_CREATE_PLAYLIST_TABLE = "CREATE TABLE " + PlaylistEntry.TABLE_NAME + " ("
                + PlaylistEntry.PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PlaylistEntry.PLAYLIST_NAME + " TEXT NOT NULL UNIQUE, "
                + PlaylistEntry.PLAYLIST_DESCRIPTION + " TEXT"
                + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_PLAYLIST_TABLE);

        String SQL_CREATE_MUSIC_IN_PLAYLIST_TABLE = "CREATE TABLE " + MusicInPlaylistEntry.TABLE_NAME + " ("
                + MusicEntry.MUSIC_ID + " INTEGER NOT NULL, "
                + MusicEntry.MUSIC_TITLE + " TEXT NOT NULL, "
                + MusicEntry.MUSIC_PATH + " TEXT NOT NULL, "
                + MusicEntry.MUSIC_DURATION + " INTEGER NOT NULL, "
                + MusicEntry.MUSIC_ARTIST + " TEXT, "
                + PlaylistEntry.PLAYLIST_NAME + " TEXT NOT NULL"
                + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_MUSIC_IN_PLAYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
