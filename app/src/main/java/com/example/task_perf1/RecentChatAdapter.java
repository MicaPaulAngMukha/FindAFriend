package com.example.task_perf1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ViewHolder> {

    private final Context context;
    private final List<String> usernames;
    private final databaseHelper db;

    public RecentChatAdapter(Context context, List<String> usernames) {
        this.context = context;
        this.usernames = usernames;
        this.db = new databaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = usernames.get(position);
        holder.usernameText.setText(username);

        // Fetch and set profile picture
        userData user = db.getUserProfile(username);
        if (user != null && user.profilePicture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.profilePicture, 0, user.profilePicture.length);
            holder.profileImage.setImageBitmap(bitmap);
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_profile_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessagingActivity.class);
            intent.putExtra("USERNAME", username);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return usernames != null ? usernames.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}