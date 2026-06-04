package com.example.chillguy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.chillguy.R;
import com.example.chillguy.activity.WorkoutDetailActivity;
import com.example.chillguy.database.AppDatabase;
import com.example.chillguy.database.WorkoutProgress;
import com.example.chillguy.helper.SharedPrefHelper;
import com.example.chillguy.model.WorkoutDataSource;
import com.example.chillguy.model.WorkoutDay;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.List;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class HomeFragment extends Fragment {

    private TextView               tvGreeting, tvCompletedText, tvPercent;
    private LinearProgressIndicator progressWeekly;
    private MaterialButton         btnStartProgram;
    private SharedPrefHelper       prefHelper;
    private final Handler          mainHandler = new Handler(Looper.getMainLooper());

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefHelper     = new SharedPrefHelper(requireContext());
        tvGreeting     = view.findViewById(R.id.tvGreeting);
        tvCompletedText= view.findViewById(R.id.tvCompletedText);
        tvPercent      = view.findViewById(R.id.tvPercent);
        progressWeekly = view.findViewById(R.id.progressWeekly);
        btnStartProgram= view.findViewById(R.id.btnStartProgram);

        tvGreeting.setText("Hello, " + prefHelper.getUsername() + "!");

        btnStartProgram.setOnClickListener(v -> {
            WorkoutDay day1 = WorkoutDataSource.get7DayPlan().get(0);
            Intent intent = new Intent(requireContext(), WorkoutDetailActivity.class);
            intent.putExtra(WorkoutDetailActivity.EXTRA_WORKOUT_DAY, day1);
            startActivity(intent);
        });

        loadProgress();
    }

    @Override public void onResume() { super.onResume(); loadProgress(); }

    private void loadProgress() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int completed = AppDatabase.getInstance(requireContext())
                    .workoutProgressDao().countCompleted();
            int percent   = (completed * 100) / 7;

            mainHandler.post(() -> {
                if (!isAdded()) return;
                tvCompletedText.setText(completed + " / 7 Days Completed");
                tvPercent.setText(percent + "%");
                progressWeekly.setProgressCompat(percent, true);
            });
        });
    }
}