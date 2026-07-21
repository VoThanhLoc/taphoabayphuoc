package com.example.taphoabayphuoc.activities.invoice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.databinding.ActivityInvoiceBinding;

public class InvoiceActivity extends AppCompatActivity {

    private ActivityInvoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}