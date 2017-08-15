package com.devenik7.android.genericmusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nisha on 06-Aug-17.
 */

public class Music implements Parcelable {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String dataPath;
    private int duration;
    private int frequency;
    private int rating;

    public Music (int Id, String Title, String Artist, String Album, String DataPath, int Duration, int Frequency, int Rating) {
        id = Id;
        title = Title;
        artist = Artist;
        album = Album;
        dataPath = DataPath;
        duration = Duration;
        frequency = Frequency;
        rating = Rating;
    }

    public Music (int Id, String Title, String Artist, String Album, String DataPath, int Duration) {
        id = Id;
        title = Title;
        artist = Artist;
        album = Album;
        dataPath = DataPath;
        duration = Duration;
        frequency = 0;
        rating = PlayerContract.MusicEntry.MUSIC_NEUTRAL;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDataPath() {
        return dataPath;
    }

    public int getDuration() {
        return duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getRating() {
        return rating;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    private Music (Parcel source) {
        this.id = source.readInt();
        this.title = source.readString();
        this.artist = source.readString();
        this.album = source.readString();
        this.dataPath = source.readString();
        this.duration = source.readInt();
        this.frequency = source.readInt();
        this.rating = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeString(this.dataPath);
        dest.writeInt(this.duration);
        dest.writeInt(this.frequency);
        dest.writeInt(this.rating);
    }

    @Override
    public String toString() {
        return "Student{"
                + "id='" + id + "\'"
                + ", title='" + title + "\'"
                + ", artist='" + artist + "\'"
                + ", album='" + album + "\'"
                + ", dataPath='" + dataPath + "\'"
                + ", duration='" + duration + "\'"
                + ", frequency='" + frequency + "\'"
                + ", rating='" + rating + "\'"
                + '}';
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
