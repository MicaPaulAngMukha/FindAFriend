package com.example.task_perf1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class databaseHelper extends SQLiteOpenHelper{
    private static final String databaseName = "FindAFriend.db";
    private static final int databaseVersion = 4; 

    public databaseHelper(Context context){
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String usersTable = "CREATE TABLE users (" +
                "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "birthDate TEXT NOT NULL," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "userBio TEXT DEFAULT 'No bio yet', " +
                "profilePicture BLOB DEFAULT NULL," +
                "isArchived BOOLEAN NOT NULL DEFAULT 0)";

        String interestsTable = "CREATE TABLE interests (" +
                "userID INTEGER REFERENCES users(userID) ON DELETE CASCADE," +
                "interest TEXT NOT NULL)";

        String messages = "CREATE TABLE messages (" +
                "MessageID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "SenderID INTEGER REFERENCES users(userID) ON DELETE CASCADE," +
                "ReceiverID INTEGER REFERENCES users(userID) ON DELETE CASCADE," +
                "MessageText TEXT NOT NULL," +
                "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

        db.execSQL(usersTable);
        db.execSQL(interestsTable);
        db.execSQL(messages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS interests");
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean addNewUser (String firstName, String lastName, String birthDate, String username, String password, String[] interests){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues userValues = new ContentValues();

        userValues.put("firstName", firstName);
        userValues.put("lastName", lastName);
        userValues.put("birthDate", birthDate);
        userValues.put("username", username);
        userValues.put("password", password);

        long userID = db.insert("users", null, userValues);

        if(userID < 0){
            return false;
        }
        else{
            if(interests != null && interests.length > 0) {
                for (String interest : interests) {
                    ContentValues iv = new ContentValues();
                    iv.put("userID", userID);
                    iv.put("interest", interest);
                    db.insert("interests", null, iv);
                }
            }
            return true;
        }
    }

    public int getUserID(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userID FROM users WHERE username = ?", new String[]{username});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    public boolean deleteUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues userValues = new ContentValues();
        userValues.put("isArchived", 1);
        int result = db.update("users", userValues, "username = ?", new String[]{username});
        return result > 0;
    }

    public boolean updateUser(String oldUsername, String newUsername, String bio, String[] interests, byte[] profilePicture) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT userID FROM users WHERE username = ?", new String[]{oldUsername});

        if (cursor.moveToFirst()) {
            int userID = cursor.getInt(0);
            cursor.close();

            ContentValues userValues = new ContentValues();
            userValues.put("username", newUsername);
            userValues.put("userBio", bio);

            if (profilePicture != null) {
                userValues.put("profilePicture", profilePicture);
            }

            db.update("users", userValues, "userID = ?", new String[]{String.valueOf(userID)});
            db.delete("interests", "userID = ?", new String[]{String.valueOf(userID)});

            if (interests != null) {
                for (String interest : interests) {
                    if (interest != null && !interest.isEmpty()) {
                        ContentValues iv = new ContentValues();
                        iv.put("userID", userID);
                        iv.put("interest", interest);
                        db.insert("interests", null, iv);
                    }
                }
            }
            return true;
        }
        if (cursor != null) cursor.close();
        return false;
    }

    public boolean LogIn(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT password FROM users WHERE username = ? AND isArchived = 0";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean success = false;
        if(cursor.moveToFirst()){
            String passwordConfirmation = cursor.getString(0);
            success = password.equals(passwordConfirmation);
        }
        cursor.close();
        return success;
    }

    public userData getUserProfile(String username){
        userData data = new userData();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

        if(cursor.moveToFirst()){
            data.username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            data.biography = cursor.getString(cursor.getColumnIndexOrThrow("userBio"));
            data.profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow("profilePicture"));
            long userID = cursor.getLong(cursor.getColumnIndexOrThrow("userID"));
            cursor.close();

            Cursor intCursor = db.rawQuery("SELECT interest FROM interests WHERE userID = ?", new String[]{String.valueOf(userID)});
            int i = 0;
            if(intCursor.moveToFirst()){
                do{
                    if(i < data.interests.length){
                        data.interests[i] = intCursor.getString(intCursor.getColumnIndexOrThrow("interest"));
                        i++;
                    }
                }while(intCursor.moveToNext());
            }
            intCursor.close();
            return data;
        }
        cursor.close();
        return null;
    }

    public List<String> loadChatHistory(String currentUsername) {
        List<String> partnerUsernames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        int currentUserID = getUserID(currentUsername);
        if (currentUserID == -1) return partnerUsernames;

        String query = "SELECT username FROM users WHERE userID IN (" +
                "SELECT ReceiverID FROM messages WHERE SenderID = ? " +
                "UNION " +
                "SELECT SenderID FROM messages WHERE ReceiverID = ?)";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentUserID), String.valueOf(currentUserID)});
        if (cursor.moveToFirst()) {
            do {
                partnerUsernames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return partnerUsernames;
    }

    public String getRandomUser(String currentUsername) {
        SQLiteDatabase db = this.getReadableDatabase();
        String interestMatchQuery = "SELECT u.username FROM users u JOIN interests i ON u.userID = i.userID " +
                "WHERE u.username != ? AND u.isArchived = 0 " +
                "AND i.interest IN (SELECT interest FROM interests WHERE userID = (SELECT userID FROM users WHERE username = ?)) " +
                "ORDER BY RANDOM() LIMIT 1";
        
        Cursor cursor = db.rawQuery(interestMatchQuery, new String[]{currentUsername, currentUsername});
        if (cursor.moveToFirst()) {
            String partner = cursor.getString(0);
            cursor.close();
            return partner;
        }
        cursor.close();

        Cursor fallback = db.rawQuery("SELECT username FROM users WHERE username != ? AND isArchived = 0 ORDER BY RANDOM() LIMIT 1", new String[]{currentUsername});
        String partner = null;
        if (fallback.moveToFirst()) {
            partner = fallback.getString(0);
        }
        fallback.close();
        return partner;
    }

    public long sendMessage(int senderID, int receiverID, String message) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("SenderID", senderID);
            values.put("ReceiverID", receiverID);
            values.put("MessageText", message);
            
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            values.put("Timestamp", currentTime);

            return db.insertOrThrow("messages", null, values);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error sending message: " + e.getMessage());
            return -1;
        }
    }

    public Cursor loadMessages(int partnerID, int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM messages WHERE (SenderID = ? AND ReceiverID = ?) " +
                "OR (SenderID = ? AND ReceiverID = ?) ORDER BY Timestamp ASC";
        return db.rawQuery(query, new String[]{String.valueOf(userID), String.valueOf(partnerID), String.valueOf(partnerID), String.valueOf(userID)});
    }

    public List<userData> loadUsersByInterest(String interestType, String currentUsername) {
        List<userData> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT u.* FROM users u " +
                "JOIN interests i ON u.userID = i.userID " +
                "WHERE i.interest = ? AND u.username != ? AND u.isArchived = 0";
        
        Cursor cursor = db.rawQuery(query, new String[]{interestType, currentUsername});

        if (cursor.moveToFirst()) {
            do {
                userData data = new userData();
                data.username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                data.biography = cursor.getString(cursor.getColumnIndexOrThrow("userBio"));
                data.profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow("profilePicture"));
                // Note: interests are not fully populated here for efficiency, 
                // but you can add a separate query if needed.
                users.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public boolean checkUser(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE username = ?", new String[]{username});

        if(cursor.moveToFirst()){
            return true;
        }

        else{
            return false;
        }
    }

    public boolean forgotPassword(String username, String newPassword){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE username = ?", new String[]{username});

        if(cursor.moveToFirst()){
            cursor.close();
            ContentValues userValues = new ContentValues();
            userValues.put("password", newPassword);
            long result = db.update("users", userValues, "username = ?", new String[]{username});
            return result > 0;
        }

        else{
            return false;
        }
    }
}
