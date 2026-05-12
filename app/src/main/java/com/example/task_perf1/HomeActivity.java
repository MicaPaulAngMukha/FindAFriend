package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final String[] interestsArr = {
        "Arts", "Painting", "Writing", "Poetry", "Games", "Online Games",
        "Board Games", "Movies", "Sports", "Books", "Volleyball", "Cats",
        "Music", "Singing", "Cooking", "Musical Instruments", "Composing", "Baking"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Removed manual padding logic to prevent "ugly" double gaps.
        // The UI will now be handled by fitsSystemWindows="true" in the XML.

        databaseHelper db = new databaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentUser = sharedPref.getString("LOGGED_IN_USER", "");

        // 1. Setup Interests
        RecyclerView interestsRecyclerView = findViewById(R.id.interests_recycler_view);
        InterestAdapter interestAdapter = new InterestAdapter(this, interestsArr);
        interestsRecyclerView.setAdapter(interestAdapter);
        interestsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // 2. Setup Profile Icon
        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        
        refreshProfileIcon(db, currentUser);

        // 3. Setup Recent Chats from Database
        List<String> chatsList = db.loadChatHistory(currentUser);

        TextView noChatsText = findViewById(R.id.no_recent_chats_text);
        RecyclerView recentChatsRecyclerView = findViewById(R.id.recent_chats_recycler_view);

        if (chatsList.isEmpty()) {
            recentChatsRecyclerView.setVisibility(View.GONE);
            noChatsText.setVisibility(View.VISIBLE);
        } else {
            recentChatsRecyclerView.setVisibility(View.VISIBLE);
            noChatsText.setVisibility(View.GONE);

            RecentChatAdapter recentChatAdapter = new RecentChatAdapter(this, chatsList);
            recentChatsRecyclerView.setAdapter(recentChatAdapter);
            recentChatsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        // 4. Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_chats) {
                startActivity(new Intent(this, ChatActivity.class));
                return true;
            } else if (itemId == R.id.navigation_friends) {
                startActivity(new Intent(this, MeetActivity.class));
                return true;
            }
            return false;
        });

        // 5. Handle Login Toast
        if (getIntent().getBooleanExtra("WELCOME_TOAST", false)) {
            Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
            getIntent().removeExtra("WELCOME_TOAST");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseHelper db = new databaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentUser = sharedPref.getString("LOGGED_IN_USER", "");
        refreshProfileIcon(db, currentUser);
    }

    private void refreshProfileIcon(databaseHelper db, String currentUser) {
        ImageView profileIcon = findViewById(R.id.profile_icon);
        userData user = db.getUserProfile(currentUser);
        if (user != null && user.profilePicture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.profilePicture, 0, user.profilePicture.length);
            profileIcon.setImageBitmap(bitmap);
        } else {
            profileIcon.setImageResource(R.drawable.icons8_profile);
        }
    }
}