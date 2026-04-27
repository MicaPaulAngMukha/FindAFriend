package com.example.task_perf1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private final Context context;
    private final String[] interests;

    public InterestAdapter(Context context, String[] interests) {
        this.context = context;
        this.interests = interests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_interest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String interest = interests[position];
        holder.interestName.setText(interest);

        int iconResId = context.getResources().getIdentifier("icons8_" + interest.toLowerCase().replace(" ", "_"), "drawable", context.getPackageName());
        if (iconResId != 0) {
            holder.interestIcon.setImageResource(iconResId);
        } else {
            holder.interestIcon.setImageResource(0); // Clear image if not found
        }

        // --- Start of Click Listener ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InterestActivity.class);
            intent.putExtra("INTEREST_NAME", interest);
            context.startActivity(intent);
        });
        // --- End of Click Listener ---
    }

    @Override
    public int getItemCount() {
        return interests.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView interestIcon;
        TextView interestName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            interestIcon = itemView.findViewById(R.id.interest_icon);
            interestName = itemView.findViewById(R.id.interest_name);
        }
    }
}