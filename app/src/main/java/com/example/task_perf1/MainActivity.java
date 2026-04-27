package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isNavigatingInternally = false;
    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getIntent().hasExtra("FROM_ACCOUNT_CREATION") && !getIntent().hasExtra("FROM_LOGOUT")) {
            Toast.makeText(this, "Created!", Toast.LENGTH_SHORT).show();
        }

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usnText = findViewById(R.id.username_input);
                EditText passText = findViewById(R.id.password_input);

                String username = usnText.getText().toString();
                String password = passText.getText().toString();

                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }

                else {
                    databaseHelper dbh = new databaseHelper(MainActivity.this);

                    if (!dbh.LogIn(username, password)) {
                        Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    } else {
                        // Save the username globally using SharedPreferences
                        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        sharedPref.edit().putString("LOGGED_IN_USER", username).apply();

                        isNavigatingInternally = true;
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("WELCOME_TOAST", true);
                        startActivity(intent);
                        finish(); // Close login so user can't go back to it
                    }
                }
            }
        });

        // Setup Forgot Password link
        TextView forgotPasswordText = findViewById(R.id.forgot_password_text);
        forgotPasswordText.setOnClickListener(v -> {
            isNavigatingInternally = true;
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        Button createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNavigatingInternally = true;
                Intent intent = new Intent(MainActivity.this, AccountCreationActivity.class);
                startActivity(intent);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                isBackPressed = true;

                Toast.makeText(getApplicationContext(), "Paused!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Stopped!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Destroyed!", Toast.LENGTH_SHORT).show();

                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() ->
                                finishAffinity(),
                        3000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getIntent().hasExtra("FROM_ACCOUNT_CREATION") && !getIntent().hasExtra("FROM_LOGOUT")) {
            Toast.makeText(this, "Resumed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!getIntent().hasExtra("FROM_ACCOUNT_CREATION") && !getIntent().hasExtra("FROM_LOGOUT")) {
            Toast.makeText(this, "Started!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingInternally && !isBackPressed) {
            Toast paused = Toast.makeText(getApplicationContext(), "Paused!", Toast.LENGTH_SHORT);
            Toast stopped = Toast.makeText(getApplicationContext(), "Stopped!", Toast.LENGTH_SHORT);
            paused.show();
            stopped.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isNavigatingInternally = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBackPressed) {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() ->
                            Toast.makeText(getApplicationContext(), "Destroyed!", Toast.LENGTH_SHORT).show(),
                    300);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "Restarted!", Toast.LENGTH_SHORT).show();
     }
}