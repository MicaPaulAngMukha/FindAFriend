package com.example.task_perf1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private databaseHelper db;
    private String verifiedUsername;
    private LinearLayout step1Container, step2Container;
    private EditText usernameInput, newPasswordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        db = new databaseHelper(this);

        // Link UI components
        step1Container = findViewById(R.id.step1_container);
        step2Container = findViewById(R.id.step2_container);
        usernameInput = findViewById(R.id.forgot_username_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_new_password_input);

        Button nextButton = findViewById(R.id.step1_next_button);
        Button changeButton = findViewById(R.id.change_password_button);
        Button backButton1 = findViewById(R.id.step1_back_button);
        Button backButton2 = findViewById(R.id.step2_back_button);

        // Step 1: Verify Username
        nextButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.checkUser(username)) {
                verifiedUsername = username;
                // Switch to password panel
                step1Container.setVisibility(View.GONE);
                step2Container.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Username doesn't exist", Toast.LENGTH_SHORT).show();
            }
        });

        // Step 2: Update Password
        changeButton.setOnClickListener(v -> {
            String newPass = newPasswordInput.getText().toString().trim();
            String confirmPass = confirmPasswordInput.getText().toString().trim();

            if (newPass.isEmpty()) {
                Toast.makeText(this, "Enter a new password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.forgotPassword(verifiedUsername, newPass)) {
                Toast.makeText(this, "Change Successful!", Toast.LENGTH_SHORT).show();
                finish(); // Goes back to Login screen (MainActivity)
            } else {
                Toast.makeText(this, "Error: Change Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation
        backButton1.setOnClickListener(v -> finish());
        backButton2.setOnClickListener(v -> {
            step2Container.setVisibility(View.GONE);
            step1Container.setVisibility(View.VISIBLE);
        });
    }
}