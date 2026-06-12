package com.example.chillguy.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_tracks")
public class CachedTrack {
    @PrimaryKey
    public long   id;
    public String title;
    public String artistName;
    public String coverUrl;
    public String previewUrl;
    public int    duration;
    public long   cachedAt;
}