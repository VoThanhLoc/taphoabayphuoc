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
import com.example.taphoabayphuoc.models.SettingEntity;
import com.example.taphoabayphuoc.models.UserEntity;
import com.example.taphoabayphuoc.repository.SettingRepository;
import com.example.taphoabayphuoc.repository.UserRepository;
import androidx.appcompat.app.AlertDialog;

import com.example.taphoabayphuoc.activities.login.LoginActivity;
import com.example.taphoabayphuoc.firebase.FirebaseManager;
import com.example.taphoabayphuoc.utils.SessionManager;
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
        SettingRepository repository =
                new SettingRepository(this);
        binding.cardLogout.setOnClickListener(v -> showLogoutDialog());
        if(repository.get() == null){

            SettingEntity setting = new SettingEntity();

            setting.setShopName("Tạp Hóa Bảy Phước");
            setting.setAddress("");
            setting.setPhone("");

            repository.insert(setting);

        }
        UserRepository userRepository =
                new UserRepository(this);

        if(userRepository.getByUsername("admin") == null){

            UserEntity admin = new UserEntity();

            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setFullName("Quản trị");
            admin.setRole("ADMIN");
            admin.setActive(true);

            userRepository.insert(admin);

        }
    }
    private void showLogoutDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {

        FirebaseManager.getAuth().signOut();

        SessionManager session = new SessionManager(this);
        session.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}