package com.example.chillguy.activity;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import com.example.chillguy.view.CircularTimerView;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.concurrent.Executors;

public class WorkoutDetailActivity extends AppCompatActivity {

    public static final String EXTRA_WORKOUT_DAY = "extra_workout_day";

    private TextView          tvDayTitle, tvTimer, tvMotivation;
    private CircularTimerView progressRing;
    private MaterialButton    btnStart, btnPause;
    private RecyclerView      rvExercises;

    private WorkoutDay      workoutDay;
    private List<Exercise>  exerciseList;
    private ExerciseAdapter exerciseAdapter;
    private CountDownTimer  countDownTimer;
    private int             currentIndex   = 0;
    private boolean         isRunning      = false;
    private long            timeLeftMillis = 0;
    private int             totalSeconds   = 0; 

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
        progressRing = findViewById(R.id.circularTimer);
        btnStart     = findViewById(R.id.btnStart);
        btnPause     = findViewById(R.id.btnPause);
        rvExercises  = findViewById(R.id.rvExercises);

        workoutDay = (WorkoutDay) getIntent().getSerializableExtra(EXTRA_WORKOUT_DAY);
        if (workoutDay == null) { finish(); return; }

        exerciseList    = workoutDay.getExercises();
        exerciseAdapter = new ExerciseAdapter(exerciseList, currentIndex);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(exerciseAdapter);

        tvDayTitle.setText(workoutDay.getProgramName() + " — Day "
                + String.format("%02d", workoutDay.getDayNumber()));

        loadExercise(currentIndex);

        btnStart.setOnClickListener(v -> { if (!isRunning) startTimer(); });
        btnPause.setOnClickListener(v -> { if (isRunning)  pauseTimer(); });
    }

    private void loadExercise(int index) {
        if (index >= exerciseList.size()) {
            onAllExercisesDone();
            return;
        }

        Exercise ex  = exerciseList.get(index);
        totalSeconds = ex.getDurationSeconds();
        timeLeftMillis = totalSeconds * 1000L;

        updateTimerDisplay(timeLeftMillis);

        progressRing.setProgress(1f);

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

        countDownTimer = new CountDownTimer(timeLeftMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimerDisplay(millisUntilFinished);

                float progress = (float) millisUntilFinished / (totalSeconds * 1000L);
                progressRing.setProgress(progress);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                progressRing.setProgress(0f);
                updateTimerDisplay(0);

                playAlarm();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    currentIndex++;
                    loadExercise(currentIndex);
                }, 1500);
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
        int secs = (int) (millis / 1000);
        tvTimer.setText(String.format("%02d:%02d", secs / 60, secs % 60));
    }

    private void playAlarm() {
        // 1. Vibrasi
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 400, 150, 400}, -1));
            } else {
                vibrator.vibrate(new long[]{0, 400, 150, 400}, -1);
            }
        }

        try {
            android.media.ToneGenerator toneGen = new android.media.ToneGenerator(
                    AudioManager.STREAM_ALARM, 100);
            toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1500);
            new Handler(Looper.getMainLooper()).postDelayed(toneGen::release, 2000);
        } catch (Exception e) {
            try {
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                android.media.Ringtone ringtone =
                        RingtoneManager.getRingtone(getApplicationContext(), uri);
                if (ringtone != null) {
                    ringtone.play();
                    new Handler(Looper.getMainLooper()).postDelayed(
                            ringtone::stop, 2000);
                }
            } catch (Exception ignored) {}
        }
    }

    private void onAllExercisesDone() {
        Executors.newSingleThreadExecutor().execute(() -> {
            WorkoutProgress progress = new WorkoutProgress();
            progress.dayNumber   = workoutDay.getDayNumber();
            progress.isCompleted = true;
            progress.completedAt = System.currentTimeMillis();
            AppDatabase.getInstance(getApplicationContext())
                    .workoutProgressDao().insertOrUpdate(progress);
        });

        new AlertDialog.Builder(this)
                .setTitle("Workout Complete! 🎉")
                .setMessage("Amazing work! Day " + workoutDay.getDayNumber()
                        + " completed. Keep glowing up! ✨")
                .setPositiveButton("Back to Plan", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override public boolean onSupportNavigateUp() { onBackPressed(); return true; }

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