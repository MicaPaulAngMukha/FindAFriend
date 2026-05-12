package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private databaseHelper db;
    private String currentUser;
    private RecyclerView chatsRecyclerView;
    private TextView noChatsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Standardized UI: Removed manual padding logic.
        // Handled by fitsSystemWindows="true" in activity_chat.xml.

        // 1. Setup Database and User info
        db = new databaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUser = sharedPref.getString("LOGGED_IN_USER", "");

        chatsRecyclerView = findViewById(R.id.chats_recycler_view);
        noChatsText = findViewById(R.id.no_recent_chats_text);

        // 2. Load Real Chat History
        refreshChatHistory();

        // 3. Setup Profile Icon
        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        
        // Load Current User Profile Pic
        refreshProfileIcon();

        // Setup Archive Button
        ImageView archiveButton = findViewById(R.id.archive_button);
        archiveButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ArchivedChatsActivity.class);
            startActivity(intent);
        });

        // 4. Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_chats);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.navigation_friends) {
                startActivity(new Intent(this, MeetActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshChatHistory();
        refreshProfileIcon();
    }

    private void refreshProfileIcon() {
        ImageView profileIcon = findViewById(R.id.profile_icon);
        userData user = db.getUserProfile(currentUser);
        if (user != null && user.profilePicture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.profilePicture, 0, user.profilePicture.length);
            profileIcon.setImageBitmap(bitmap);
        } else {
            profileIcon.setImageResource(R.drawable.icons8_profile);
        }
    }

    private void refreshChatHistory() {
        List<String> chatsList = db.loadChatHistory(currentUser);

        if (chatsList.isEmpty()) {
            chatsRecyclerView.setVisibility(View.GONE);
            noChatsText.setVisibility(View.VISIBLE);
        } else {
            chatsRecyclerView.setVisibility(View.VISIBLE);
            noChatsText.setVisibility(View.GONE);

            ChatAdapter adapter = new ChatAdapter(this, chatsList);
            adapter.setOnItemLongClickListener(username -> showArchiveDialog(username));
            chatsRecyclerView.setAdapter(adapter);
            chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void showArchiveDialog(String partnerUsername) {
        new AlertDialog.Builder(this)
                .setTitle("Archive Chat")
                .setMessage("Are you sure you want to archive this chat?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int myID = db.getUserID(currentUser);
                    int partnerID = db.getUserID(partnerUsername);
                    if (db.archiveChat(myID, partnerID)) {
                        refreshChatHistory();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}