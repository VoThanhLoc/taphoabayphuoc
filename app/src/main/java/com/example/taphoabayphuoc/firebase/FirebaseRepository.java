package com.example.taphoabayphuoc.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.example.taphoabayphuoc.models.Product;
public class FirebaseRepository {
    private final DatabaseReference root;
    public FirebaseRepository() {
        root = FirebaseManager.getRoot();
    }
    public void createUserIfNotExists(String uid, String email, Runnable onComplete) {
        Log.d("FIREBASE", "createUserIfNotExists");
        DatabaseReference userRef = root.child("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("SYNC", "Firebase count = " + snapshot.getChildrenCount());
                if (snapshot.exists()) {

                    onComplete.run();

                    return;
                }

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
                            Log.d("FIREBASE", "Write SUCCESS");
                            onComplete.run();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FIREBASE", "Write FAILED: " + e.getMessage());
                            onComplete.run();
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });
    }

    private DatabaseReference getProductRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return root.child("users")
                .child(uid)
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
        Log.d("SYNC", "loadProducts()");
        getProductRef()
                .addListenerForSingleValueEvent(listener);

    }
}