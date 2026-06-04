package com.example.chillguy.fragment;

import android.media.MediaPlayer;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.chillguy.R;
import com.example.chillguy.adapter.MusicAdapter;
import com.example.chillguy.helper.NetworkHelper;
import com.example.chillguy.model.Track;
import com.example.chillguy.network.RetrofitClient;
import com.example.chillguy.model.DeezerResponse;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MusicFragment extends Fragment {
    private RecyclerView      rvMusic;
    private ProgressBar       progressLoading;
    private LinearLayout      layoutError;
    private MaterialButton    btnRefresh;
    private TextView          tvErrorMsg, tvNowPlaying;
    private MusicAdapter      adapter;
    private MediaPlayer       mediaPlayer;
    private int               playingPosition = -1;
    private final Handler     mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService   executor    = Executors.newSingleThreadExecutor();
    private static final String[] QUERIES = {
            "workout motivation", "gym music", "fitness beats",
            "lofi workout", "energetic workout"
    };
    private int queryIndex = 0;

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

        adapter.setOnPlayClickListener((track, position) -> handlePlayPause(track, position));

        btnRefresh.setOnClickListener(v -> fetchMusic(QUERIES[queryIndex % QUERIES.length]));

        fetchMusic(QUERIES[queryIndex]);
    }

    private void fetchMusic(String query) {
        if (!isAdded()) return;

        if (!NetworkHelper.isConnected(requireContext())) {
            showError("No internet connection.\nShowing cached results.");
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
                        showError("No tracks found. Try refreshing.");
                    }
                });

            } catch (IOException e) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    showLoading(false);
                    showError("Connection failed.\nCheck your internet and retry.");
                });
            }
        });
    }

    private void handlePlayPause(Track track, int position) {
        if (playingPosition == position) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                tvNowPlaying.setText("Paused");
            } else if (mediaPlayer != null) {
                mediaPlayer.start();
                tvNowPlaying.setText("▶ " + track.getTitle());
            }
        } else {
            stopMedia();

            if (track.getPreviewUrl() != null && !track.getPreviewUrl().isEmpty()) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(track.getPreviewUrl());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(mp -> {
                        mp.start();
                        tvNowPlaying.setText("▶ " + track.getTitle() + " – " + track.getArtistName());
                    });
                    mediaPlayer.setOnCompletionListener(mp -> {
                        playingPosition = -1;
                        adapter.setPlayingPosition(-1);
                        tvNowPlaying.setText("");
                    });
                    playingPosition = position;
                    adapter.setPlayingPosition(position);
                } catch (IOException e) {
                    Toast.makeText(requireContext(), "Cannot play preview", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "No preview available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        playingPosition = -1;
        adapter.setPlayingPosition(-1);
        tvNowPlaying.setText("");
    }

    private void showLoading(boolean show) {
        progressLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        rvMusic.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) layoutError.setVisibility(View.GONE);
    }

    private void showError(String msg) {
        layoutError.setVisibility(View.VISIBLE);
        rvMusic.setVisibility(View.GONE);
        tvErrorMsg.setText(msg);
    }

    @Override public void onPause()   { super.onPause();   stopMedia(); }
    @Override public void onDestroy() { super.onDestroy(); stopMedia(); if (executor != null) executor.shutdown(); }
}