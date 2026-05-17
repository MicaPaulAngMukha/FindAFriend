FindAFriend - Local Chatting & Discovery App
FindAFriend is a feature-rich Android application designed to facilitate local communication and social discovery based on shared interests. Developed primarily as a high-quality school project or social experimentation tool, it demonstrates advanced Android development concepts including local database management, complex UI/UX patterns, and robust data security.
🚀 Purpose
The app aims to bridge the gap between social discovery and private messaging. It allows users to create detailed profiles, discover others with similar hobbies (Arts, Gaming, Sports, etc.), and engage in private conversations—all while maintaining full control over their privacy through blocking and archiving systems.
✨ Key Features
🔐 Security & Account Management
•
Complex Password Checker: Enforces high security with requirements for uppercase, lowercase, numbers, special characters, and a minimum length of 8 characters.
•
Password Toggle: Integrated visibility toggles on all password fields for a user-friendly login/registration experience.
•
Secure Reset: A multi-step "Forgot Password" flow to regain access to accounts.
•
Safe Deletion: A 90-day account deletion policy that allows users to reverse their decision by logging back in before the period ends.
🤝 Discovery & Matching
•
Smart Matching System: A "Connect Now" feature that pairs you with random users or those who share your interests.
•
Anti-Redundancy Logic: The matching algorithm ensures you aren't re-matched with people you already have an existing chat history with.
•
Interest-Based Search: Browse users categorized by specific hobbies using a clean, icon-based grid.
💬 Messaging & Privacy
•
Local Chat History: Persistent messaging history stored securely on the device.
•
Archive System: Long-press chats to move them to a dedicated Archive tab. Archived chats are read-only until unarchived.
•
Blocking System: Block users to instantly remove them from your chat list and prevent them from appearing in your matching or discovery feeds again.
•
View-Only Profiles: Click on a chatmate's name to view their bio and interests without being able to edit their data.
🛠 Technical Overview
Database Architecture
The app utilizes SQLite (via databaseHelper.java) as its primary engine. It manages five core tables:
1.
Users: Stores credentials, bios, and profile pictures (as Blobs).
2.
Interests: Relates users to their chosen hobbies with cascading deletes.
3.
Messages: Handles the sender/receiver relationships and timestamps.
4.
Archived Chats: Tracks which conversations are hidden from the main feed.
5.
Blocked Users: A many-to-many table that filters the global user pool for discovery.
UI/UX Implementation
•
Fragment-Based Registration: Uses a ViewPager2 and Fragments for a smooth, multi-step account creation process.
•
Custom Adapters: Efficient data binding using RecyclerView for Recent Chats, Message Bubbles, and Interest Cards.
•
Modern Aesthetics: Implements a "Regular App Look" with a standardized status bar that matches the UI and respects system window insets to prevent clashing with the time/battery icons.
📦 Tech Stack
•
Language: Java
•
Database: SQLite
•
UI Components: Google Material Design (Chips, TextInputLayout, ShapeableImageView)
•
Minimum SDK: API 24 (Android 7.0)
•
Target SDK: API 35/36 (Android 15)
🔧 Installation
1.
Clone the repository: git clone https://github.com/YourUsername/FindAFriend.git
2.
Open the project in Android Studio.
3.
Sync the project with Gradle files.
4.
Ensure you have the required icons in the res/drawable folder (e.g., icons8_arts, icons8_hide, etc.).
5.
Run the app on an emulator or physical device.

Feel free to use it for school projects
•
How to build a clean, multi-activity navigation flow with a Bottom Navigation Bar.
Created with ❤️ for social connection.
