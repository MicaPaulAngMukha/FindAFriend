package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. Setup Database and User info
        databaseHelper db = new databaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentUser = sharedPref.getString("LOGGED_IN_USER", "");

        // 2. Load Real Chat History
        List<String> chatsList = db.loadChatHistory(currentUser);

        RecyclerView chatsRecyclerView = findViewById(R.id.chats_recycler_view);
        TextView noChatsText = findViewById(R.id.no_recent_chats_text);

        if (chatsList.isEmpty()) {
            chatsRecyclerView.setVisibility(View.GONE);
            noChatsText.setVisibility(View.VISIBLE);
        } else {
            chatsRecyclerView.setVisibility(View.VISIBLE);
            noChatsText.setVisibility(View.GONE);

            ChatAdapter adapter = new ChatAdapter(this, chatsList);
            chatsRecyclerView.setAdapter(adapter);
            chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        // 3. Setup Profile Icon
        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

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
}