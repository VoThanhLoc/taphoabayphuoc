package com.example.taphoabayphuoc.activities.camera;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.databinding.ActivityCameraBinding;

public class CameraActivity extends AppCompatActivity {

    private ActivityCameraBinding binding;

    private String mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCameraBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mode = getIntent().getStringExtra("mode");

        if (mode == null) {
            mode = CameraMode.PHOTO;
        }

        initCamera();

        initView();
    }

    private void initView() {

        if (CameraMode.BARCODE.equals(mode)) {

            binding.btnCapture.hide();

        } else {

            binding.btnCapture.show();

        }

    }

    private void initCamera() {

        // CameraX
    }

}