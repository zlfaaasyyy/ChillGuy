package com.example.chillguy.model;

import com.google.gson.annotations.SerializedName;

public class Track {
    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("duration")
    private int duration;

    @SerializedName("preview")
    private String previewUrl;

    @SerializedName("artist")
    private Artist artist;

    @SerializedName("album")
    private Album album;

    public static class Artist {
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
    }

    public static class Album {
        @SerializedName("cover_medium")
        private String coverMedium;
        public String getCoverMedium() { return coverMedium; }
    }

    public long   getId()         { return id; }
    public String getTitle()      { return title; }
    public int    getDuration()   { return duration; }
    public String getPreviewUrl() { return previewUrl; }

    public String getArtistName() {
        return (artist != null) ? artist.getName() : "Unknown Artist";
    }

    public String getCoverUrl() {
        return (album != null) ? album.getCoverMedium() : null;
    }

    public String getDurationFormatted() {
        int m = duration / 60;
        int s = duration % 60;
        return String.format("%d:%02d", m, s);
    }
}