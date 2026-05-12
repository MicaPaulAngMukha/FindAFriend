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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private String username;
    private boolean isOwnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Fix for status bar clashing - apply padding to the header only
        View header = findViewById(R.id.header);
        if (header != null) {
            ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        ImageView backArrow = findViewById(R.id.back_arrow);
        Button logoutButton = findViewById(R.id.logout_button);
        TextView editButton = findViewById(R.id.edit_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        String viewUsername = getIntent().getStringExtra("VIEW_USERNAME");
        isOwnProfile = (viewUsername == null);

        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = isOwnProfile ? sharedPref.getString("LOGGED_IN_USER", null) : viewUsername;

        if (!isOwnProfile) {
            if (editButton != null) editButton.setVisibility(View.GONE);
            if (logoutButton != null) logoutButton.setVisibility(View.GONE);
            if (bottomNavigationView != null) bottomNavigationView.setVisibility(View.GONE);
        }

        backArrow.setOnClickListener(v -> finish());

        if (isOwnProfile) {
            editButton.setOnClickListener(v -> {
                startActivity(new Intent(this, EditProfileActivity.class));
            });

            logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

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
        
        refreshProfileData();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    sharedPref.edit().remove("LOGGED_IN_USER").apply();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("FROM_LOGOUT", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOwnProfile) {
            refreshProfileData();
        }
    }

    private void refreshProfileData() {
        if (username == null) return;
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