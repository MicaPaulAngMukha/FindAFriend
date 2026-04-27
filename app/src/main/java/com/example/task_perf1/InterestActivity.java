package com.example.task_perf1;

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

public class InterestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);

        databaseHelper db = new databaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String myUsername = sharedPref.getString("LOGGED_IN_USER", "");

        TextView interestTitle = findViewById(R.id.interest_title);
        ImageView backArrow = findViewById(R.id.back_arrow);
        RecyclerView recyclerView = findViewById(R.id.users_recycler_view);
        TextView noUsersText = findViewById(R.id.no_users_text);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        String interest = getIntent().getStringExtra("INTEREST_NAME");
        if (interest != null) {
            interestTitle.setText(interest);
            
            // 1. Load users from database
            List<userData> userList = db.loadUsersByInterest(interest, myUsername);

            // 2. Setup UI based on results
            if (userList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noUsersText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noUsersText.setVisibility(View.GONE);
                
                UserAdapter adapter = new UserAdapter(this, userList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            }
        }

        backArrow.setOnClickListener(v -> finish());
        bottomNavigationView.setSelectedItemId(R.id.navigation_friends);
    }
}