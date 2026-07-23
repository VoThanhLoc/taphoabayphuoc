package com.example.taphoabayphuoc.repository;

import androidx.annotation.NonNull;

import com.example.taphoabayphuoc.firebase.FirebaseManager;
import com.example.taphoabayphuoc.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ProductFirebaseRepository {

    private final DatabaseReference productRef;

    public ProductFirebaseRepository() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        productRef = FirebaseManager.getDatabase()
                .getReference("users")
                .child(uid)
                .child("products");

    }

    public void insert(Product product) {

        String key = productRef.push().getKey();

        if (key == null) return;
        product.setFirebaseId(key);
        productRef.child(key).setValue(product);

    }

    public void update(Product product) {

        if (product.getFirebaseId() == null) return;

        productRef
                .child(product.getFirebaseId())
                .setValue(product);

    }

    public void delete(Product product) {

        if (product.getFirebaseId() == null) return;

        productRef
                .child(product.getFirebaseId())
                .removeValue();

    }

}