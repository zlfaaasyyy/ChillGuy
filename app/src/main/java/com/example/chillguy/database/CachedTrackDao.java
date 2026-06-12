package com.example.chillguy.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CachedTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedTrack> tracks);

    @Query("SELECT * FROM cached_tracks ORDER BY cachedAt DESC")
    List<CachedTrack> getAllCached();

    @Query("DELETE FROM cached_tracks")
    void clearAll();

    @Query("SELECT COUNT(*) FROM cached_tracks")
    int count();
}