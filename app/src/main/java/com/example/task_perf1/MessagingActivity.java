package com.example.task_perf1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    private databaseHelper db;
    private int myID, partnerID;
    private String partnerUsername;
    private EditText messageInput;
    private LinearLayout emptyState;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private LinearLayout inputArea;
    private TextView archivedNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Fix for navigation bar overlap
        View mainLayout = findViewById(R.id.main_messaging_layout);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        db = new databaseHelper(this);

        // 1. Setup UI
        TextView usernameTitle = findViewById(R.id.username_title);
        ImageView backArrow = findViewById(R.id.back_arrow);
        messageInput = findViewById(R.id.message_input);
        ImageView sendButton = findViewById(R.id.send_button);
        emptyState = findViewById(R.id.empty_state);
        recyclerView = findViewById(R.id.messages_recycler_view);
        inputArea = findViewById(R.id.input_area);
        archivedNotice = findViewById(R.id.archived_notice);
        ImageView menuButton = findViewById(R.id.menu_button);

        // 2. Get User IDs
        partnerUsername = getIntent().getStringExtra("USERNAME");
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String myUsername = sharedPref.getString("LOGGED_IN_USER", "");

        myID = db.getUserID(myUsername);
        partnerID = db.getUserID(partnerUsername);

        usernameTitle.setText(partnerUsername);
        usernameTitle.setPaintFlags(usernameTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        backArrow.setOnClickListener(v -> finish());

        // 3. Setup Click on Username to view profile
        usernameTitle.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("VIEW_USERNAME", partnerUsername);
            startActivity(intent);
        });

        // Check if archived
        checkArchivedStatus();

        // 4. Setup RecyclerView
        adapter = new MessageAdapter(messageList, myID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 5. Setup Send Button
        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (msg.isEmpty()) return;

            if (myID == -1 || partnerID == -1) {
                Toast.makeText(this, "Internal Error: IDs not found", Toast.LENGTH_LONG).show();
                return;
            }

            long result = db.sendMessage(myID, partnerID, msg);
            if (result != -1) {
                messageInput.setText("");
                refreshMessages();
            } else {
                Toast.makeText(this, "DB Error: Failed to insert message", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. Setup Menu Button
        menuButton.setOnClickListener(this::showPopupMenu);

        refreshMessages();
    }

    private void checkArchivedStatus() {
        if (db.isChatArchived(myID, partnerID)) {
            inputArea.setVisibility(View.GONE);
            archivedNotice.setVisibility(View.VISIBLE);
        } else {
            inputArea.setVisibility(View.VISIBLE);
            archivedNotice.setVisibility(View.GONE);
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.messaging_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_block) {
                showBlockDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showBlockDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Block User")
                .setMessage("Are you sure you want to block " + partnerUsername + "? The chat history will disappear and you won't see them again.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (db.blockUser(myID, partnerID)) {
                        Toast.makeText(this, "User blocked", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void refreshMessages() {
        if (myID == -1 || partnerID == -1) return;

        Cursor cursor = db.loadMessages(partnerID, myID);
        messageList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            do {
                String text = cursor.getString(cursor.getColumnIndexOrThrow("MessageText"));
                int sender = cursor.getInt(cursor.getColumnIndexOrThrow("SenderID"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("Timestamp"));
                messageList.add(new Message(text, sender, time));
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
        if (!messageList.isEmpty()) {
            recyclerView.scrollToPosition(messageList.size() - 1);
        }
    }
}