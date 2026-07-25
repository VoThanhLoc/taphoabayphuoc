package com.example.taphoabayphuoc.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.taphoabayphuoc.firebase.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class AuthRepository {

    public interface LoginCallback {
        void onSuccess();

        void onError(String message);
    }

    public void login(String email,
                      String password,
                      LoginCallback callback) {

        FirebaseManager.getAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseManager.getRoot()
                                    .child("test")
                                    .setValue("Hello")
                                    .addOnSuccessListener(unused ->
                                            Log.d("TEST axclv", "WRITE SUCCESS"))
                                    .addOnFailureListener(e ->
                                            Log.e("TEST axclv", "WRITE FAIL", e));
                            callback.onSuccess();

                        } else {

                            String message = "Đăng nhập thất bại";

                            if (task.getException() != null) {
                                message = task.getException().getMessage();
                            }

                            callback.onError(message);
                        }
                    }
                });
    }
}