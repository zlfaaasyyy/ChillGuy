package com.example.chillguy.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_progress")
public class WorkoutProgress {
    @PrimaryKey
    public int     dayNumber;
    public boolean isCompleted;
    public long    completedAt;
}