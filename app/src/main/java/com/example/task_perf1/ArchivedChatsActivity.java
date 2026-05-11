package com.example.task_perf1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArchivedChatsActivity extends AppCompatActivity {

    private databaseHelper db;
    private String currentUser;
    private RecyclerView recyclerView;
    private TextView noArchivedText;
    private ChatAdapter adapter;
    private List<String> archivedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_chats);

        db = new databaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUser = sharedPref.getString("LOGGED_IN_USER", "");

        ImageView backButton = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.archived_chats_recycler_view);
        noArchivedText = findViewById(R.id.no_archived_chats_text);

        backButton.setOnClickListener(v -> finish());

        loadArchivedChats();
    }

    private void loadArchivedChats() {
        archivedList = db.loadArchivedChats(currentUser);

        if (archivedList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noArchivedText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noArchivedText.setVisibility(View.GONE);

            adapter = new ChatAdapter(this, archivedList);
            // Override long click for unarchiving
            adapter.setOnItemLongClickListener(username -> showUnarchiveDialog(username));
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void showUnarchiveDialog(String partnerUsername) {
        new AlertDialog.Builder(this)
                .setTitle("Unarchive Chat")
                .setMessage("Are you sure you want to unarchive this chat?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int myID = db.getUserID(currentUser);
                    int partnerID = db.getUserID(partnerUsername);
                    if (db.unarchiveChat(myID, partnerID)) {
                        loadArchivedChats();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}