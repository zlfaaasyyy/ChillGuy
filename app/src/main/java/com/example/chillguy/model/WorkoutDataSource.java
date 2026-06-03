package com.example.chillguy.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkoutDataSource {

    public static List<WorkoutDay> get7DayPlan() {
        List<WorkoutDay> plan = new ArrayList<>();

        plan.add(new WorkoutDay(1, "Full Body Warm Up", "Full Body", 20,
                Arrays.asList(
                        new Exercise("Neck Rolls",       30, 0, "self_improvement"),
                        new Exercise("Arm Circles",      30, 0, "sports_gymnastics"),
                        new Exercise("Hip Circles",      30, 0, "rotate_right"),
                        new Exercise("Jumping Jacks",    45, 0, "directions_run"),
                        new Exercise("High Knees",       40, 0, "directions_walk"),
                        new Exercise("Body Twist",       30, 0, "sync_alt"),
                        new Exercise("Forward Fold",     45, 0, "south")
                )
        ));

        plan.add(new WorkoutDay(2, "Core & Abs Burn", "Core", 25,
                Arrays.asList(
                        new Exercise("Plank Hold",       60, 0, "fitness_center"),
                        new Exercise("Crunches",          0,20, "repeat"),
                        new Exercise("Bicycle Crunches",  0,20, "loop"),
                        new Exercise("Leg Raises",        0,15, "south"),
                        new Exercise("Mountain Climbers",45, 0, "terrain"),
                        new Exercise("Russian Twist",     0,20, "sync_alt"),
                        new Exercise("Dead Bug",         45, 0, "bug_report")
                )
        ));

        plan.add(new WorkoutDay(3, "Lower Body Stretch", "Lower Body", 20,
                Arrays.asList(
                        new Exercise("Standing Quad Stretch", 30, 0, "accessibility"),
                        new Exercise("Hamstring Stretch",     45, 0, "south"),
                        new Exercise("Hip Flexor Lunge",      40, 0, "directions_walk"),
                        new Exercise("Butterfly Stretch",     45, 0, "self_improvement"),
                        new Exercise("Calf Raises",            0,20, "arrow_upward"),
                        new Exercise("Pigeon Pose",           45, 0, "spa"),
                        new Exercise("Child's Pose",          60, 0, "airline_seat_flat")
                )
        ));

        plan.add(new WorkoutDay(4, "Glute Glow Session", "Glutes", 30,
                Arrays.asList(
                        new Exercise("Glute Bridges",     0,20, "fitness_center"),
                        new Exercise("Donkey Kicks",      0,15, "sports_gymnastics"),
                        new Exercise("Fire Hydrants",     0,15, "local_fire_department"),
                        new Exercise("Sumo Squats",       0,20, "accessibility"),
                        new Exercise("Side Lying Clams",  0,20, "spa"),
                        new Exercise("Hip Thrust Hold",  45,  0, "airline_seat_flat"),
                        new Exercise("Standing Kickbacks",0,15, "directions_run")
                )
        ));

        plan.add(new WorkoutDay(5, "Upper Body Tone", "Upper Body", 25,
                Arrays.asList(
                        new Exercise("Shoulder Rolls",   30,  0, "sync_alt"),
                        new Exercise("Push Ups",          0, 12, "fitness_center"),
                        new Exercise("Tricep Dips",       0, 15, "arrow_downward"),
                        new Exercise("Arm Pulses",        0, 30, "sports_gymnastics"),
                        new Exercise("Chest Opener",     45,  0, "open_in_full"),
                        new Exercise("Superman Hold",    30,  0, "flight"),
                        new Exercise("Cat-Cow Stretch",  45,  0, "pets")
                )
        ));

        plan.add(new WorkoutDay(6, "Cardio Flow", "Full Body", 30,
                Arrays.asList(
                        new Exercise("Jumping Jacks",    45,  0, "directions_run"),
                        new Exercise("Burpees",           0, 10, "bolt"),
                        new Exercise("Jump Squats",       0, 15, "arrow_upward"),
                        new Exercise("Speed Skaters",    45,  0, "sports"),
                        new Exercise("High Knees",       40,  0, "directions_walk"),
                        new Exercise("Box Step",          0, 20, "grid_on"),
                        new Exercise("Cool Down Walk",   60,  0, "directions_walk")
                )
        ));

        plan.add(new WorkoutDay(7, "Gentle Strength", "Full Body", 25,
                Arrays.asList(
                        new Exercise("Deep Breathing",   60,  0, "air"),
                        new Exercise("Sun Salutation",   90,  0, "wb_sunny"),
                        new Exercise("Warrior I",        45,  0, "self_improvement"),
                        new Exercise("Warrior II",       45,  0, "self_improvement"),
                        new Exercise("Tree Pose",        30,  0, "park"),
                        new Exercise("Seated Forward Fold",45,0,"south"),
                        new Exercise("Final Savasana",   120, 0, "hotel")
                )
        ));

        return plan;
    }
}