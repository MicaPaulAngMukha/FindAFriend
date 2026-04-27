package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Random;

public class MeetActivity extends AppCompatActivity {

    private final String[] interestsArr = {
        "Arts", "Painting", "Writing", "Poetry", "Games", "Online Games",
        "Board Games", "Movies", "Sports", "Books", "Volleyball", "Cats",
        "Music", "Singing", "Cooking", "Musical Instruments", "Composing", "Baking"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        RecyclerView interestsRecyclerView = findViewById(R.id.interests_recycler_view);
        InterestAdapter adapter = new InterestAdapter(this, interestsArr);
        interestsRecyclerView.setAdapter(adapter);
        interestsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // Handle Connect Now button
        Button connectNowButton = findViewById(R.id.connect_now_button);
        connectNowButton.setOnClickListener(v -> {
            databaseHelper db = new databaseHelper(this);
            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String currentUser = sharedPref.getString("LOGGED_IN_USER", "");

            // Ask the database to pick a random partner directly
            String randomPartner = db.getRandomUser(currentUser);
            
            if (randomPartner != null) {
                Toast.makeText(this, "Connecting with " + randomPartner, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MessagingActivity.class);
                intent.putExtra("USERNAME", randomPartner);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No users available to connect with yet!", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_friends);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.navigation_chats) {
                startActivity(new Intent(this, ChatActivity.class));
                return true;
            }
            return false;
        });
    }
}