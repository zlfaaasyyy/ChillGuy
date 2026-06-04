package com.example.chillguy.fragment;

import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.chillguy.R;
import com.example.chillguy.activity.WelcomeActivity;
import com.example.chillguy.database.AppDatabase;
import com.example.chillguy.helper.SharedPrefHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView       tvUsername, tvEmail;
    private TextView       tvTotalWorkouts, tvActiveDays;
    private SwitchMaterial switchDarkMode;
    private MaterialButton btnLogout;
    private SharedPrefHelper prefHelper;
    private final Handler  mainHandler = new Handler(Looper.getMainLooper());

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefHelper = new SharedPrefHelper(requireContext());

        tvUsername       = view.findViewById(R.id.tvUsername);
        tvEmail          = view.findViewById(R.id.tvEmail);
        tvTotalWorkouts  = view.findViewById(R.id.tvTotalWorkouts);
        tvActiveDays     = view.findViewById(R.id.tvActiveDays);
        switchDarkMode   = view.findViewById(R.id.switchDarkMode);
        btnLogout        = view.findViewById(R.id.btnLogout);

        tvUsername.setText(prefHelper.getUsername());
        tvEmail.setText(prefHelper.getEmail());

        switchDarkMode.setOnCheckedChangeListener(null);
        switchDarkMode.setChecked(prefHelper.isDarkTheme());

        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            prefHelper.setDarkTheme(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
            requireActivity().recreate();
        });

        btnLogout.setOnClickListener(v -> {
            prefHelper.logout();
            Intent intent = new Intent(requireContext(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadStats();
    }

    @Override public void onResume() { super.onResume(); loadStats(); }

    private void loadStats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int completed = AppDatabase.getInstance(requireContext())
                    .workoutProgressDao().countCompleted();

            mainHandler.post(() -> {
                if (!isAdded()) return;
                tvTotalWorkouts.setText(String.valueOf(completed));
                tvActiveDays.setText(completed + " / 7");
            });
        });
    }
}