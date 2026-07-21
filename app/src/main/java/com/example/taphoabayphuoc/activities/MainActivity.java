package com.example.taphoabayphuoc.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.activities.invoice.InvoiceActivity;
import com.example.taphoabayphuoc.activities.product.ProductActivity;
import com.example.taphoabayphuoc.activities.sale.SaleActivity;
import com.example.taphoabayphuoc.activities.setting.SettingActivity;
import com.example.taphoabayphuoc.databinding.ActivityMainBinding;

/**
 * MainActivity serves as the main dashboard of the application.
 * It provides a central navigation hub to access different functional modules
 * such as Sales, Product Management, Invoice History, and Settings.
 */
public class MainActivity extends AppCompatActivity {
    /** View binding for the activity_main layout */
    private ActivityMainBinding binding;

    /**
     * Initializes the activity, enables edge-to-edge display, and sets up 
     * navigation listeners for the dashboard UI components.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardSale.setOnClickListener(v ->
                startActivity(new Intent(this, SaleActivity.class)));

        binding.cardProduct.setOnClickListener(v ->
                startActivity(new Intent(this, ProductActivity.class)));

        binding.cardInvoice.setOnClickListener(v ->
                startActivity(new Intent(this, InvoiceActivity.class)));

        binding.cardSetting.setOnClickListener(v ->
                startActivity(new Intent(this, SettingActivity.class)));
    }
}