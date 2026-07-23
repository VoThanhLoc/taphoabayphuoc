package com.example.taphoabayphuoc.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    private static final FirebaseDatabase database =
            FirebaseDatabase.getInstance(
                    "https://taphoabayphuoc-4cbe1-default-rtdb.asia-southeast1.firebasedatabase.app"
            );

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static DatabaseReference getRoot() {
        return database.getReference();
    }
}