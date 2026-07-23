package com.example.taphoabayphuoc.activities.setting;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.databinding.ActivitySettingBinding;
import com.example.taphoabayphuoc.models.SettingEntity;
import com.example.taphoabayphuoc.repository.SettingRepository;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;

    private SettingRepository repository;

    private SettingEntity setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        repository = new SettingRepository(this);

        setting = repository.get();

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadSetting();

        binding.btnSave.setOnClickListener(v -> saveSetting());

    }

    private void loadSetting(){

        if(setting==null) return;

        binding.edtShopName.setText(setting.getShopName());

        binding.edtAddress.setText(setting.getAddress());

        binding.edtPhone.setText(setting.getPhone());

    }

    private void saveSetting(){

        setting.setShopName(binding.edtShopName.getText().toString());

        setting.setAddress(binding.edtAddress.getText().toString());

        setting.setPhone(binding.edtPhone.getText().toString());

        repository.update(setting);

        Toast.makeText(this,"Đã lưu",Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

}