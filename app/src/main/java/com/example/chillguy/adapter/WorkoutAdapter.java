package com.example.chillguy.adapter;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chillguy.R;
import com.example.chillguy.model.WorkoutDay;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
    private final List<WorkoutDay> data;
    private OnItemClickListener listener;
    public interface OnItemClickListener { void onItemClick(WorkoutDay day); }
    public WorkoutAdapter(List<WorkoutDay> data) { this.data = data; }
    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_day, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        WorkoutDay day = data.get(pos);
        h.tvDayNumber.setText(String.format("%02d", day.getDayNumber()));
        h.tvProgramName.setText(day.getProgramName());
        h.tvTarget.setText(day.getTargetArea() + " · " + day.getTotalMinutes() + " min");
        h.ivCheck.setVisibility(day.isCompleted() ? View.VISIBLE : View.INVISIBLE);
        h.card.setOnClickListener(v -> { if (listener != null) listener.onItemClick(day); });
        h.card.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
            else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
            return false;
        });
    }

    @Override public int getItemCount() { return data.size(); }

    public void updateCompletedDays(List<Integer> completedDayNumbers) {
        for (WorkoutDay day : data) day.setCompleted(completedDayNumbers.contains(day.getDayNumber()));
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card; TextView tvDayNumber, tvProgramName, tvTarget; ImageView ivCheck;
        ViewHolder(View v) {
            super(v);
            card = v.findViewById(R.id.cardWorkout); tvDayNumber = v.findViewById(R.id.tvDayNumber);
            tvProgramName = v.findViewById(R.id.tvProgramName); tvTarget = v.findViewById(R.id.tvTarget);
            ivCheck = v.findViewById(R.id.ivCheck);
        }
    }
}