package com.example.chillguy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.*;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.chillguy.R;
import com.example.chillguy.activity.WorkoutDetailActivity;
import com.example.chillguy.adapter.WorkoutAdapter;
import com.example.chillguy.database.AppDatabase;
import com.example.chillguy.database.WorkoutProgress;
import com.example.chillguy.model.WorkoutDataSource;
import com.example.chillguy.model.WorkoutDay;
import java.util.*;
import java.util.concurrent.Executors;

public class WorkoutFragment extends Fragment {

    private WorkoutAdapter   adapter;
    private TextView         tvCompletionCount, tvStreakCount;
    private final Handler    mainHandler = new Handler(Looper.getMainLooper());
    private List<WorkoutDay> workoutList;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCompletionCount = view.findViewById(R.id.tvCompletionCount);
        tvStreakCount     = view.findViewById(R.id.tvStreakCount);
        RecyclerView rv   = view.findViewById(R.id.rvWorkoutDays);

        workoutList = WorkoutDataSource.get7DayPlan();
        adapter     = new WorkoutAdapter(workoutList);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(day -> {
            Intent intent = new Intent(requireContext(), WorkoutDetailActivity.class);
            intent.putExtra(WorkoutDetailActivity.EXTRA_WORKOUT_DAY, day);
            startActivity(intent);
        });

        loadProgressFromDb();
    }

    @Override public void onResume() { super.onResume(); loadProgressFromDb(); }

    private void loadProgressFromDb() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<WorkoutProgress> progressList = AppDatabase
                    .getInstance(requireContext())
                    .workoutProgressDao()
                    .getAllProgress();

            List<Integer> completedDays = new ArrayList<>();
            for (WorkoutProgress p : progressList) {
                if (p.isCompleted) completedDays.add(p.dayNumber);
            }

            int streak = 0;
            for (int i = 1; i <= 7; i++) {
                if (completedDays.contains(i)) streak++;
                else break;
            }
            final int finalStreak    = streak;
            final int finalCompleted = completedDays.size();
            final List<Integer> finalDays = completedDays;

            mainHandler.post(() -> {
                if (!isAdded()) return;
                adapter.updateCompletedDays(finalDays);
                tvCompletionCount.setText(finalCompleted + "/7 Days");
                tvStreakCount.setText(finalStreak + " Days");
            });
        });
    }
}