package com.example.chillguy.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.example.chillguy.R;
import com.example.chillguy.activity.MainActivity;

import java.io.IOException;

public class MusicService extends Service {
    public static final String CHANNEL_ID      = "ChillGuyMusicChannel";
    public static final int    NOTIFICATION_ID  = 101;

    public static final String ACTION_PLAY      = "ACTION_PLAY";
    public static final String ACTION_PAUSE     = "ACTION_PAUSE";
    public static final String ACTION_STOP      = "ACTION_STOP";

    private MediaPlayer        mediaPlayer;
    private MediaSessionCompat mediaSession;
    private final IBinder      binder = new MusicBinder();

    private String  currentTitle      = "";
    private String  currentArtist     = "";
    private String  currentPreviewUrl = "";
    private boolean isPlaying         = false;

    public interface OnPlaybackListener {
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackStopped();
        void onPlaybackCompleted();
        void onError(String message);
    }
    private OnPlaybackListener playbackListener;

    public void setPlaybackListener(OnPlaybackListener listener) {
        this.playbackListener = listener;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        setupMediaSession();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    resumeMusic();
                    break;
                case ACTION_PAUSE:
                    pauseMusic();
                    break;
                case ACTION_STOP:
                    stopMusicAndService();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (mediaSession != null) mediaSession.release();
    }

    public void playTrack(String previewUrl, String title, String artist) {
        currentTitle      = title;
        currentArtist     = artist;
        currentPreviewUrl = previewUrl;

        releasePlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        try {
            mediaPlayer.setDataSource(previewUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                showNotification(true);
                if (playbackListener != null) playbackListener.onPlaybackStarted();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
                showNotification(false);
                if (playbackListener != null) playbackListener.onPlaybackCompleted();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                isPlaying = false;
                if (playbackListener != null) playbackListener.onError("Cannot play preview");
                return true;
            });
        } catch (IOException e) {
            if (playbackListener != null) playbackListener.onError("Invalid preview URL");
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
            showNotification(false);
            if (playbackListener != null) playbackListener.onPlaybackPaused();
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showNotification(true);
            if (playbackListener != null) playbackListener.onPlaybackStarted();
        }
    }

    public void stopMusicAndService() {
        releasePlayer();
        isPlaying = false;
        stopForeground(true);
        stopSelf();
        if (playbackListener != null) playbackListener.onPlaybackStopped();
    }

    public boolean isPlaying()         { return isPlaying; }
    public String  getCurrentTitle()   { return currentTitle; }
    public String  getCurrentArtist()  { return currentArtist; }

    private void showNotification(boolean playing) {
        Intent openApp = new Intent(this, MainActivity.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingOpen = PendingIntent.getActivity(
                this, 0, openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pendingPlay = PendingIntent.getService(
                this, 1, playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPause = PendingIntent.getService(
                this, 2, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent pendingStop = PendingIntent.getService(
                this, 3, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(currentTitle.isEmpty() ? "ChillGuy Music" : currentTitle)
                .setContentText(currentArtist.isEmpty() ? "Workout Vibes" : currentArtist)
                .setContentIntent(pendingOpen)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSilent(true)
                .setOngoing(playing)
                .addAction(
                        playing ? R.drawable.ic_pause : R.drawable.ic_play,
                        playing ? "Pause" : "Play",
                        playing ? pendingPause : pendingPlay)
                .addAction(R.drawable.ic_close, "Stop", pendingStop)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));

        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "ChillGuy Music Player",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Music playback controls");
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void setupMediaSession() {
        mediaSession = new MediaSessionCompat(this, "ChillGuySession");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setActive(true);

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override public void onPlay()  { resumeMusic(); }
            @Override public void onPause() { pauseMusic(); }
            @Override public void onStop()  { stopMusicAndService(); }
        });
    }

    private void updatePlaybackState(int state) {
        if (mediaSession == null) return;
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_STOP)
                .setState(state, 0, 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}