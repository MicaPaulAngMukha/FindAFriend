package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView backArrow = findViewById(R.id.back_arrow);
        Button logoutButton = findViewById(R.id.logout_button);
        TextView editButton = findViewById(R.id.edit_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Check if we are viewing someone else's profile
        String viewUsername = getIntent().getStringExtra("VIEW_USERNAME");
        boolean isOwnProfile = (viewUsername == null);

        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String username = isOwnProfile ? sharedPref.getString("LOGGED_IN_USER", null) : viewUsername;

        if (!isOwnProfile) {
            // Hide Edit and Logout buttons if viewing someone else
            if (editButton != null) editButton.setVisibility(View.GONE);
            if (logoutButton != null) logoutButton.setVisibility(View.GONE);
            if (bottomNavigationView != null) bottomNavigationView.setVisibility(View.GONE);
        }

        if (username != null) {
            setupProfileData(username);
        }

        backArrow.setOnClickListener(v -> finish());

        if (isOwnProfile) {
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
            });

            logoutButton.setOnClickListener(v -> {
                sharedPref.edit().remove("LOGGED_IN_USER").apply();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("FROM_LOGOUT", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_chats) {
                    startActivity(new Intent(this, ChatActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_friends) {
                    startActivity(new Intent(this, MeetActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String viewUsername = getIntent().getStringExtra("VIEW_USERNAME");
        if (viewUsername == null) {
            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String username = sharedPref.getString("LOGGED_IN_USER", null);
            if (username != null) {
                setupProfileData(username);
            }
        }
    }

    private void setupProfileData(String username) {
        databaseHelper db = new databaseHelper(this);
        userData user = db.getUserProfile(username);

        if (user != null) {
            TextView usernameTitle = findViewById(R.id.username_title);
            TextView usernameDisplay = findViewById(R.id.username_display);
            TextView bioText = findViewById(R.id.bio_text);
            ShapeableImageView profileImage = findViewById(R.id.profile_image);
            
            if (usernameTitle != null) usernameTitle.setText(user.username);
            if (usernameDisplay != null) usernameDisplay.setText("@" + user.username);
            if (bioText != null) bioText.setText(user.biography);

            if (user.profilePicture != null && profileImage != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(user.profilePicture, 0, user.profilePicture.length);
                profileImage.setImageBitmap(bitmap);
            }

            ChipGroup chipGroup = findViewById(R.id.profile_interest_chip_group);
            if (chipGroup != null && user.interests != null) {
                chipGroup.removeAllViews();
                for (String interest : user.interests) {
                    if (interest != null && !interest.isEmpty()) {
                        Chip chip = new Chip(this);
                        chip.setText(interest);
                        chipGroup.addView(chip);
                    }
                }
            }
        }
    }
}