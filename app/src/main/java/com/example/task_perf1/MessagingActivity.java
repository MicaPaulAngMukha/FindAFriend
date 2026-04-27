package com.example.task_perf1;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        db = new databaseHelper(this);

        // 1. Setup UI
        TextView usernameTitle = findViewById(R.id.username_title);
        ImageView backArrow = findViewById(R.id.back_arrow);
        messageInput = findViewById(R.id.message_input);
        ImageView sendButton = findViewById(R.id.send_button);
        emptyState = findViewById(R.id.empty_state);
        recyclerView = findViewById(R.id.messages_recycler_view);

        // 2. Get User IDs
        partnerUsername = getIntent().getStringExtra("USERNAME");
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String myUsername = sharedPref.getString("LOGGED_IN_USER", "");

        myID = db.getUserID(myUsername);
        partnerID = db.getUserID(partnerUsername);

        usernameTitle.setText(partnerUsername);
        backArrow.setOnClickListener(v -> finish());

        // 3. Setup RecyclerView
        adapter = new MessageAdapter(messageList, myID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 4. Setup Send Button
        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (msg.isEmpty()) return;

            if (myID == -1 || partnerID == -1) {
                Toast.makeText(this, "Internal Error: IDs not found (" + myID + "/" + partnerID + ")", Toast.LENGTH_LONG).show();
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

        refreshMessages();
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