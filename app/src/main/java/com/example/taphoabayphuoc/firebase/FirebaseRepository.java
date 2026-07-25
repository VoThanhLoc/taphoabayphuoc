package com.example.taphoabayphuoc.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import com.example.taphoabayphuoc.models.Product;
public class FirebaseRepository {
    private final DatabaseReference root;
    public FirebaseRepository() {
        root = FirebaseManager.getRoot();
        Log.d("FIREBASE_INIT", "Database URL: " + FirebaseManager.getDatabase().getReference().toString());
        monitorConnection();
    }

    private void monitorConnection() {
        DatabaseReference connectedRef = FirebaseManager.getDatabase().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected != null && connected) {
                    Log.d("FIREBASE_CONN", "Connected to Firebase Database");
                } else {
                    Log.w("FIREBASE_CONN", "Disconnected from Firebase Database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE_CONN", "Connection monitor cancelled: " + error.getMessage());
            }
        });
    }

    public void createUserIfNotExists(String uid, String email, Runnable onComplete) {

        Log.d("FIREBASE", "createUserIfNotExists()");
        Log.d("FIREBASE", "UID = " + uid);

        if (uid == null || uid.isEmpty()) {
            Log.e("FIREBASE", "UID is null or empty");
            return;
        }

        DatabaseReference userRef = root.child("users").child(uid);

        Log.d("FIREBASE", "Path = " + userRef.toString());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("FIREBASE", "onDataChange()");
                Log.d("FIREBASE", "Exists = " + snapshot.exists());

                if (snapshot.exists()) {
                    Log.d("FIREBASE", "User already exists");
                    onComplete.run();
                    return;
                }

                Log.d("FIREBASE", "Creating new user...");

                Map<String, Object> data = new HashMap<>();

                Map<String, Object> profile = new HashMap<>();
                profile.put("email", email);
                profile.put("shopName", "");
                profile.put("owner", "");
                profile.put("createdAt", System.currentTimeMillis());

                data.put("profile", profile);
                data.put("products", new HashMap<>());
                data.put("invoices", new HashMap<>());
                data.put("settings", new HashMap<>());
                data.put("categories", new HashMap<>());

                userRef.setValue(data)
                        .addOnSuccessListener(unused -> {
                            Log.d("FIREBASE", "Create user success");
                            onComplete.run();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FIREBASE", "Create user failed", e);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("FIREBASE", "Cancelled");
                Log.e("FIREBASE", "Code = " + error.getCode());
                Log.e("FIREBASE", "Message = " + error.getMessage());
                Log.e("FIREBASE", "Details = " + error.getDetails());
                Log.e("FIREBASE", "Exception", error.toException());
            }
        });
    }

    private DatabaseReference getProductRef() {

        FirebaseUser user = FirebaseManager.getAuth().getCurrentUser();

        if (user == null) {
            Log.e("SYNC", "CurrentUser is NULL");
            // Return a dummy path to avoid crashing or loading other users' data
            return root.child("unauthorized");
        }

        Log.d("SYNC", "UID = " + user.getUid());

        return root.child("users")
                .child(user.getUid())
                .child("products");
    }

    public void insertProduct(Product product) {
        DatabaseReference productRef = getProductRef();
        String key = productRef.push().getKey();
        if (key == null) {
            return;
        }
        product.setFirebaseId(key);
        productRef.child(key).setValue(product);
    }

    public void updateProduct(Product product) {
        if (product.getFirebaseId() == null) {
            return;
        }
        getProductRef()
                .child(product.getFirebaseId())
                .setValue(product);
    }

    public void deleteProduct(Product product) {
        if (product.getFirebaseId() == null) {
            return;
        }
        getProductRef()
                .child(product.getFirebaseId())
                .removeValue();
    }

    public void loadProducts(ValueEventListener listener) {
        Log.d("SYNC", "loadProducts() - setting up listener");
        DatabaseReference ref = getProductRef();
        Log.d("SYNC", "Loading from path: " + ref.toString());
        
        // Ensure data is synced even when offline
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(listener);
    }
}