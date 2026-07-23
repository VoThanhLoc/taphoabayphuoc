package com.example.taphoabayphuoc.repository;

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