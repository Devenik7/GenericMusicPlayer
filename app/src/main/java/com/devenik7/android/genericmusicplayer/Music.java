package com.devenik7.android.genericmusicplayer;

/**
 * Created by nisha on 06-Aug-17.
 */

class Music {
    private int id;
    private String title;
    private String display_name;
    private String artist;
    private String dataPath;
    private int duration;
    private int frequency;
    private int rating;

    Music (int Id, String Title, String Display, String Artist, String DataPath, int Duration, int Frequency, int Rating) {
        id = Id;
        title = Title;
        display_name = Display;
        artist = Artist;
        dataPath = DataPath;
        duration = Duration;
        frequency = Frequency;
        rating = Rating;
    }

    Music (int Id, String Title, String Display, String Artist, String DataPath, int Duration) {
        id = Id;
        title = Title;
        display_name = Display;
        artist = Artist;
        dataPath = DataPath;
        duration = Duration;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getArtist() {
        return artist;
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
}
