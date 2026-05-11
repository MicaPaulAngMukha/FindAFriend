package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private String currentUsername;
    private EditText usernameInput, bioInput;
    private ChipGroup chipGroup;
    private ShapeableImageView profileImage;
    private byte[] selectedImageBytes = null;
    private databaseHelper db;
    private final String[] allInterests = {
        "Arts", "Painting", "Writing", "Poetry", "Games", "Online Games",
        "Board Games", "Movies", "Sports", "Books", "Volleyball", "Cats",
        "Music", "Singing", "Cooking", "Musical Instruments", "Composing", "Baking"
    };

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = new databaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUsername = sharedPref.getString("LOGGED_IN_USER", "");

        usernameInput = findViewById(R.id.username_input);
        bioInput = findViewById(R.id.bio_input);
        chipGroup = findViewById(R.id.interest_chip_group);
        profileImage = findViewById(R.id.profile_image);
        TextView changeProfileText = findViewById(R.id.change_profile_text);
        TextView saveButton = findViewById(R.id.save_button);
        ImageView backArrow = findViewById(R.id.back_arrow);
        Button deleteAccountButton = findViewById(R.id.delete_account_button);

        loadUserData();

        backArrow.setOnClickListener(v -> finish());

        // Make both the image and the text clickable to open files
        View.OnClickListener pickImageListener = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            String[] mimeTypes = {"image/png", "image/jpeg", "image/jpg"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        };

        profileImage.setOnClickListener(pickImageListener);
        changeProfileText.setOnClickListener(pickImageListener);

        saveButton.setOnClickListener(v -> {
            String newUsername = usernameInput.getText().toString().trim();
            String newBio = bioInput.getText().toString().trim();
            String[] selectedInterests = getSelectedInterests();

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateUser(currentUsername, newUsername, newBio, selectedInterests, selectedImageBytes)) {
                sharedPref.edit().putString("LOGGED_IN_USER", newUsername).apply();
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Notify that profile was updated
                finish();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAccountButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? Your account information will be deleted in 90 days. You can log in before 90 days to reverse the decision.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (db.deleteUser(currentUsername)) {
                        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        sharedPref.edit().clear().apply();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(this, "Account deletion in progress.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void loadUserData() {
        userData user = db.getUserProfile(currentUsername);
        if (user != null) {
            usernameInput.setText(user.username);
            bioInput.setText(user.biography);
            if (user.profilePicture != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(user.profilePicture, 0, user.profilePicture.length);
                profileImage.setImageBitmap(bitmap);
                selectedImageBytes = user.profilePicture;
            }
            setupInterests(user.interests);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Display the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);

                // Convert to byte array for database
                selectedImageBytes = getBytesFromUri(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void setupInterests(String[] userInterests) {
        chipGroup.removeAllViews();
        for (String interest : allInterests) {
            Chip chip = new Chip(this);
            chip.setText(interest);
            chip.setCheckable(true);
            
            if (userInterests != null) {
                for (String userInterest : userInterests) {
                    if (interest.equals(userInterest)) {
                        chip.setChecked(true);
                        break;
                    }
                }
            }
            chipGroup.addView(chip);
        }
    }

    private String[] getSelectedInterests() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selected.add(chip.getText().toString());
            }
        }
        return selected.toArray(new String[0]);
    }
}