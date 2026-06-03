package com.example.chillguy.model;

import java.io.Serializable;
import java.util.List;

public class WorkoutDay implements Serializable {
    private int            dayNumber;
    private String         programName;
    private String         targetArea;
    private int            totalMinutes;
    private List<Exercise> exercises;
    private boolean        isCompleted;

    public WorkoutDay(int dayNumber, String programName, String targetArea,
                      int totalMinutes, List<Exercise> exercises) {
        this.dayNumber    = dayNumber;
        this.programName  = programName;
        this.targetArea   = targetArea;
        this.totalMinutes = totalMinutes;
        this.exercises    = exercises;
        this.isCompleted  = false;
    }

    public int            getDayNumber()    { return dayNumber; }
    public String         getProgramName()  { return programName; }
    public String         getTargetArea()   { return targetArea; }
    public int            getTotalMinutes() { return totalMinutes; }
    public List<Exercise> getExercises()    { return exercises; }
    public boolean        isCompleted()     { return isCompleted; }
    public void           setCompleted(boolean v) { isCompleted = v; }
}