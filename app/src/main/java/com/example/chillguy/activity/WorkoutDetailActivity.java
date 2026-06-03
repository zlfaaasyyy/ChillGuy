package com.example.chillguy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillguy.R;
import com.example.chillguy.adapter.ExerciseAdapter;
import com.example.chillguy.database.AppDatabase;
import com.example.chillguy.database.WorkoutProgress;
import com.example.chillguy.model.Exercise;
import com.example.chillguy.model.WorkoutDay;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.concurrent.Executors;

public class WorkoutDetailActivity extends AppCompatActivity {

    public static final String EXTRA_WORKOUT_DAY = "extra_workout_day";

    private TextView     tvDayTitle, tvTimer, tvMotivation;
    private ProgressBar  progressRing;
    private MaterialButton btnStart, btnPause;
    private RecyclerView rvExercises;

    private WorkoutDay      workoutDay;
    private List<Exercise>  exerciseList;
    private ExerciseAdapter exerciseAdapter;
    private CountDownTimer  countDownTimer;
    private int             currentIndex     = 0;
    private boolean         isRunning        = false;
    private long            timeLeftMillis   = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        tvDayTitle   = findViewById(R.id.tvDayTitle);
        tvTimer      = findViewById(R.id.tvTimer);
        tvMotivation = findViewById(R.id.tvMotivation);
        progressRing = findViewById(R.id.progressRing);
        btnStart     = findViewById(R.id.btnStart);
        btnPause     = findViewById(R.id.btnPause);
        rvExercises  = findViewById(R.id.rvExercises);

        workoutDay = (WorkoutDay) getIntent().getSerializableExtra(EXTRA_WORKOUT_DAY);
        if (workoutDay == null) {
            finish();
            return;
        }

        exerciseList = workoutDay.getExercises();

        exerciseAdapter = new ExerciseAdapter(exerciseList, currentIndex);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(exerciseAdapter);

        tvDayTitle.setText(workoutDay.getProgramName() + " — Day " +
                String.format("%02d", workoutDay.getDayNumber()));

        loadExercise(currentIndex);

        btnStart.setOnClickListener(v -> {
            if (!isRunning) startTimer();
        });

        btnPause.setOnClickListener(v -> {
            if (isRunning) pauseTimer();
        });
    }

    private void loadExercise(int index) {
        if (index >= exerciseList.size()) {
            onAllExercisesDone();
            return;
        }

        Exercise ex = exerciseList.get(index);
        timeLeftMillis = ex.getDurationSeconds() * 1000L;

        updateTimerDisplay(timeLeftMillis);
        progressRing.setMax(ex.getDurationSeconds());
        progressRing.setProgress(0);

        exerciseAdapter.setCurrentIndex(index);
        exerciseAdapter.notifyDataSetChanged();

        rvExercises.smoothScrollToPosition(Math.min(index + 1, exerciseList.size() - 1));

        isRunning = false;
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
    }

    private void startTimer() {
        isRunning = true;
        btnStart.setEnabled(false);
        btnPause.setEnabled(true);

        Exercise currentEx = exerciseList.get(currentIndex);
        int totalSeconds   = currentEx.getDurationSeconds();

        countDownTimer = new CountDownTimer(timeLeftMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimerDisplay(millisUntilFinished);

                int elapsed = (int) ((totalSeconds * 1000L - millisUntilFinished) / 1000);
                progressRing.setProgress(elapsed);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                progressRing.setProgress(totalSeconds);
                currentIndex++;
                loadExercise(currentIndex);
            }
        }.start();
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isRunning = false;
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
    }

    private void updateTimerDisplay(long millis) {
        int totalSecs = (int) (millis / 1000);
        int mins      = totalSecs / 60;
        int secs      = totalSecs % 60;
        tvTimer.setText(String.format("%02d:%02d", mins, secs));
    }

    private void onAllExercisesDone() {
        Executors.newSingleThreadExecutor().execute(() -> {
            WorkoutProgress progress = new WorkoutProgress();
            progress.dayNumber   = workoutDay.getDayNumber();
            progress.isCompleted = true;
            progress.completedAt = System.currentTimeMillis();

            AppDatabase.getInstance(getApplicationContext())
                    .workoutProgressDao()
                    .insertOrUpdate(progress);
        });

        new AlertDialog.Builder(this)
                .setTitle("Workout Complete! 🎉")
                .setMessage("Amazing work! Day " + workoutDay.getDayNumber() +
                        " completed. Keep glowing up! ✨")
                .setPositiveButton("Back to Plan", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRunning) pauseTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}