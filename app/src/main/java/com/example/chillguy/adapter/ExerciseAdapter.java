package com.example.chillguy.adapter;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chillguy.R;
import com.example.chillguy.model.Exercise;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private final List<Exercise> data;
    private int currentIndex;
    public ExerciseAdapter(List<Exercise> data, int currentIndex) { this.data = data; this.currentIndex = currentIndex; }
    public void setCurrentIndex(int idx) { this.currentIndex = idx; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Exercise ex = data.get(pos);
        h.tvName.setText(ex.getName());
        h.tvLabel.setText(ex.getLabel());
        h.card.setCardBackgroundColor(h.card.getContext().getColor(
                pos == currentIndex ? R.color.light_primary_container : R.color.light_surface_container_low));
        h.card.setAlpha(pos < currentIndex ? 0.5f : 1f);
    }

    @Override public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card; TextView tvName, tvLabel;
        ViewHolder(View v) { super(v); card = v.findViewById(R.id.cardExercise); tvName = v.findViewById(R.id.tvExerciseName); tvLabel = v.findViewById(R.id.tvExerciseLabel); }
    }
}