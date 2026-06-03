package com.example.chillguy.model;

import java.io.Serializable;

public class Exercise implements Serializable {
    private String name;
    private int    durationSeconds;
    private int    reps;
    private String iconName;

    public Exercise(String name, int durationSeconds, int reps, String iconName) {
        this.name            = name;
        this.durationSeconds = durationSeconds;
        this.reps            = reps;
        this.iconName        = iconName;
    }

    public String getName()            { return name; }
    public int    getDurationSeconds() { return durationSeconds; }
    public int    getReps()            { return reps; }
    public String getIconName()        { return iconName; }

    public String getLabel() {
        if (reps > 0) return reps + " reps";
        int mins = durationSeconds / 60;
        int secs = durationSeconds % 60;
        if (mins > 0) return String.format("%d:%02d min", mins, secs);
        return durationSeconds + " sec";
    }
}