package com.example.chillguy.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WorkoutProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(WorkoutProgress progress);

    @Query("SELECT * FROM workout_progress ORDER BY dayNumber ASC")
    List<WorkoutProgress> getAllProgress();

    @Query("SELECT * FROM workout_progress WHERE dayNumber = :day LIMIT 1")
    WorkoutProgress getByDay(int day);

    @Query("SELECT COUNT(*) FROM workout_progress WHERE isCompleted = 1")
    int countCompleted();
}