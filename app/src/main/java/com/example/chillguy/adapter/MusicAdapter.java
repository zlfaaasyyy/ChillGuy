package com.example.chillguy.adapter;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.chillguy.R;
import com.example.chillguy.model.Track;
import java.util.*;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<Track> data = new ArrayList<>();
    private int playingPosition = -1;
    private OnPlayClickListener listener;
    public interface OnPlayClickListener { void onPlayClick(Track track, int position); }
    public void setOnPlayClickListener(OnPlayClickListener l) { this.listener = l; }
    public void setData(List<Track> newData) { this.data = newData; notifyDataSetChanged(); }
    public void setPlayingPosition(int pos) {
        int old = playingPosition; playingPosition = pos;
        if (old != -1) notifyItemChanged(old);
        if (pos != -1) notifyItemChanged(pos);
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Track track = data.get(pos);
        boolean isPlaying = pos == playingPosition;
        h.tvTitle.setText(track.getTitle());
        h.tvArtist.setText(track.getArtistName());
        h.tvDuration.setText(track.getDurationFormatted());
        Glide.with(h.ivCover.getContext()).load(track.getCoverUrl())
                .placeholder(R.drawable.ic_music_placeholder)
                .transform(new RoundedCorners(16)).into(h.ivCover);
        h.btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        h.itemView.setBackgroundResource(isPlaying ? R.drawable.bg_music_card_active : R.drawable.bg_music_card);
        h.btnPlay.setOnClickListener(v -> { if (listener != null) listener.onPlayClick(track, pos); });
        h.itemView.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
            else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
            return false;
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover; TextView tvTitle, tvArtist, tvDuration; ImageButton btnPlay;
        ViewHolder(View v) {
            super(v);
            ivCover = v.findViewById(R.id.ivCover); tvTitle = v.findViewById(R.id.tvTitle);
            tvArtist = v.findViewById(R.id.tvArtist); tvDuration = v.findViewById(R.id.tvDuration);
            btnPlay = v.findViewById(R.id.btnPlay);
        }
    }
}