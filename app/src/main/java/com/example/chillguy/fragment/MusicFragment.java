package com.example.chillguy.fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.chillguy.R;
import com.example.chillguy.adapter.MusicAdapter;
import com.example.chillguy.helper.NetworkHelper;
import com.example.chillguy.model.Track;
import com.example.chillguy.network.RetrofitClient;
import com.example.chillguy.model.DeezerResponse;
import com.example.chillguy.service.MusicService;
import com.google.android.material.button.MaterialButton;
import retrofit2.Response;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MusicFragment extends Fragment implements MusicService.OnPlaybackListener {
    private RecyclerView   rvMusic;
    private ProgressBar    progressLoading;
    private LinearLayout   layoutError;
    private MaterialButton btnRefresh;
    private TextView       tvErrorMsg, tvNowPlaying;
    private MusicAdapter   adapter;
    private int            playingPosition = -1;
    private MusicService   musicService;
    private boolean        isBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) binder;
            musicService = musicBinder.getService();
            musicService.setPlaybackListener(MusicFragment.this);
            isBound = true;

            if (musicService.isPlaying()) {
                tvNowPlaying.setText("▶ " + musicService.getCurrentTitle()
                        + " – " + musicService.getCurrentArtist());
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private final Handler         mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor    = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMusic         = view.findViewById(R.id.rvMusic);
        progressLoading = view.findViewById(R.id.progressLoading);
        layoutError     = view.findViewById(R.id.layoutError);
        btnRefresh      = view.findViewById(R.id.btnRefresh);
        tvErrorMsg      = view.findViewById(R.id.tvErrorMsg);
        tvNowPlaying    = view.findViewById(R.id.tvNowPlaying);

        adapter = new MusicAdapter();
        rvMusic.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMusic.setAdapter(adapter);

        adapter.setOnPlayClickListener(this::handlePlayPause);
        btnRefresh.setOnClickListener(v -> fetchMusic("workout motivation"));

        requestNotificationPermission();
        bindMusicService();
        fetchMusic("workout motivation");
    }

    @Override
    public void onStart() {
        super.onStart();
        bindMusicService();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            musicService.setPlaybackListener(null);
            requireContext().unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void bindMusicService() {
        if (!isBound) {
            Intent serviceIntent = new Intent(requireContext(), MusicService.class);
            ContextCompat.startForegroundService(requireContext(), serviceIntent);
            requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void fetchMusic(String query) {
        if (!isAdded()) return;
        if (!NetworkHelper.isConnected(requireContext())) {
            showError("No internet connection.\nTap Refresh to try again.");
            return;
        }

        showLoading(true);
        executor.execute(() -> {
            try {
                Response<DeezerResponse> response = RetrofitClient
                        .getDeezerService()
                        .searchTracks(query, 20)
                        .execute();

                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getData() != null
                            && !response.body().getData().isEmpty()) {
                        adapter.setData(response.body().getData());
                        layoutError.setVisibility(View.GONE);
                        rvMusic.setVisibility(View.VISIBLE);
                    } else {
                        showError("No tracks found. Tap Refresh.");
                    }
                });
            } catch (IOException e) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    showLoading(false);
                    showError("Connection failed.\nTap Refresh to try again.");
                });
            }
        });
    }

    private void handlePlayPause(Track track, int position) {
        if (!isBound || musicService == null) return;

        if (playingPosition == position) {
            if (musicService.isPlaying()) {
                musicService.pauseMusic();
            } else {
                musicService.resumeMusic();
            }
        } else {
            int old = playingPosition;
            playingPosition = position;
            if (old != -1) adapter.setPlayingPosition(-1);

            if (track.getPreviewUrl() != null && !track.getPreviewUrl().isEmpty()) {
                musicService.playTrack(
                        track.getPreviewUrl(),
                        track.getTitle(),
                        track.getArtistName()
                );
                adapter.setPlayingPosition(position);
            } else {
                Toast.makeText(requireContext(), "No preview available", Toast.LENGTH_SHORT).show();
                playingPosition = -1;
            }
        }
    }

    @Override
    public void onPlaybackStarted() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            if (musicService != null)
                tvNowPlaying.setText("▶ " + musicService.getCurrentTitle()
                        + " – " + musicService.getCurrentArtist());
            adapter.setPlayingPosition(playingPosition);
        });
    }

    @Override
    public void onPlaybackPaused() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            tvNowPlaying.setText("⏸ Paused");
            adapter.setPlayingPosition(playingPosition);
        });
    }

    @Override
    public void onPlaybackStopped() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            tvNowPlaying.setText("");
            playingPosition = -1;
            adapter.setPlayingPosition(-1);
        });
    }

    @Override
    public void onPlaybackCompleted() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            tvNowPlaying.setText("");
            playingPosition = -1;
            adapter.setPlayingPosition(-1);
        });
    }

    @Override
    public void onError(String message) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        );
    }

    private void showLoading(boolean show) {
        progressLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        rvMusic.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) layoutError.setVisibility(View.GONE);
    }

    private void showError(String msg) {
        layoutError.setVisibility(View.VISIBLE);
        rvMusic.setVisibility(View.GONE);
        progressLoading.setVisibility(View.GONE);
        tvErrorMsg.setText(msg);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdown();
    }
}