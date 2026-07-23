package com.example.taphoabayphuoc.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.activities.MainActivity;
import com.example.taphoabayphuoc.databinding.ActivityLoginBinding;
import com.example.taphoabayphuoc.firebase.FirebaseManager;
import com.example.taphoabayphuoc.firebase.FirebaseRepository;
import com.example.taphoabayphuoc.repository.AuthRepository;
import com.example.taphoabayphuoc.utils.SessionManager;
import com.example.taphoabayphuoc.repository.ProductRepository;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initEvent();
    }

    private void initView() {
        authRepository = new AuthRepository();
    }

    private void initEvent() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtAccount.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            if (email.isEmpty()) {
                binding.edtAccount.setError("Nhập email");
                return;
            }

            if (password.isEmpty()) {
                binding.edtPassword.setError("Nhập mật khẩu");
                return;
            }

            authRepository.login(email, password, new AuthRepository.LoginCallback() {


                @Override
                public void onSuccess() {
                    SessionManager session = new SessionManager(LoginActivity.this);
                    session.setLogin(true);
                    session.saveUid(
                            FirebaseManager.getAuth()
                                    .getCurrentUser()
                                    .getUid()
                    );
                    session.saveEmail(
                            FirebaseManager.getAuth()
                                    .getCurrentUser()
                                    .getEmail()
                    );
                    FirebaseRepository firebaseRepository = new FirebaseRepository();
                    ProductRepository productRepository =
                            new ProductRepository(LoginActivity.this);
                    firebaseRepository.createUserIfNotExists(
                            FirebaseManager.getAuth()
                                    .getCurrentUser()
                                    .getUid(),
                            FirebaseManager.getAuth()
                                    .getCurrentUser()
                                    .getEmail(),
                            () -> {
                                Log.d("LOGIN", "Start sync products");
                                productRepository.syncProductsFromFirebase(
                                        new ProductRepository.SyncCallback() {
                                            @Override
                                            public void onSuccess() {

                                                Log.d("LOGIN", "Sync success");

                                                Intent intent = new Intent(
                                                        LoginActivity.this,
                                                        MainActivity.class);

                                                startActivity(intent);
                                                finish();
                                            }
                                            @Override
                                            public void onError(String message) {

                                                Toast.makeText(
                                                        LoginActivity.this,
                                                        message,
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        });
                            });
                }
                @Override
                public void onError(String message) {

                    Toast.makeText(LoginActivity.this,
                            message,
                            Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}