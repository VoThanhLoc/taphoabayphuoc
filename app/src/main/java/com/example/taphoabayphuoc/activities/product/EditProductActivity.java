package com.example.taphoabayphuoc.activities.product;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.taphoabayphuoc.R;
import com.example.taphoabayphuoc.databinding.ActivityEditProductBinding;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductRepository;
import com.example.taphoabayphuoc.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditProductActivity extends AppCompatActivity {

    private ActivityEditProductBinding binding;
    private ProductRepository repository;
    private Product product;
    private Uri tempImageUri;
    private String savedImagePath;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    savedImagePath = FileUtils.saveImageToInternal(this, uri);
                    if (savedImagePath != null) {
                        Glide.with(this).load(new File(savedImagePath)).placeholder(R.drawable.ic_product).into(binding.imgProduct);
                    }
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && tempImageUri != null) {
                    savedImagePath = FileUtils.saveImageToInternal(this, tempImageUri);
                    if (savedImagePath != null) {
                        Glide.with(this).load(new File(savedImagePath)).placeholder(R.drawable.ic_product).into(binding.imgProduct);
                    }
                } else {
                    Log.e("CAMERA", "TakePicture failed or cancelled. success=" + success);
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Cần quyền Camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new ProductRepository(getApplicationContext());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        int id = getIntent().getIntExtra("id", -1);
        product = repository.getById(id);
        if (product == null) {
            finish();
            return;
        }

        loadData();

        binding.edtBarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                binding.edtImportPrice.requestFocus();
                return true;
            }
            return false;
        });

        binding.btnChooseImage.setOnClickListener(v -> showImageDialog());
        binding.btnSave.setOnClickListener(v -> updateProduct());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadData() {
        binding.edtName.setText(product.getName());
        binding.edtBarcode.setText(product.getBarcode());
        binding.edtImportPrice.setText(String.valueOf(product.getImportPrice()));
        binding.edtSellPrice.setText(String.valueOf(product.getSellPrice()));
        binding.edtQuantity.setText(String.valueOf(product.getQuantity()));
        binding.chkActive.setChecked(product.isActive());

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(FileUtils.getGlidePath(product.getImageUrl()))
                    .placeholder(R.drawable.ic_product)
                    .into(binding.imgProduct);
        }
    }

    private void updateProduct() {
        String name = binding.edtName.getText().toString().trim();
        String sellPriceText = binding.edtSellPrice.getText().toString().trim();

        if (name.isEmpty()) {
            binding.edtName.setError("Nhập tên sản phẩm");
            return;
        }
        if (sellPriceText.isEmpty()) {
            binding.edtSellPrice.setError("Nhập giá bán");
            return;
        }

        product.setName(name);
        product.setBarcode(binding.edtBarcode.getText().toString().trim().replace("\n", "").replace("\r", ""));
        product.setSellPrice(Double.parseDouble(sellPriceText));
        product.setImportPrice(TextUtils.isEmpty(binding.edtImportPrice.getText()) ? 0 : Double.parseDouble(binding.edtImportPrice.getText().toString()));
        product.setQuantity(TextUtils.isEmpty(binding.edtQuantity.getText()) ? 0 : Integer.parseInt(binding.edtQuantity.getText().toString()));
        product.setActive(binding.chkActive.isChecked());
        
        if (savedImagePath != null) {
            product.setImageUrl(savedImagePath);
        }

        repository.update(product);
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showImageDialog() {
        String[] items = {"📷 Chụp ảnh", "🖼 Chọn từ thư viện"};
        new AlertDialog.Builder(this)
                .setTitle("Ảnh sản phẩm")
                .setItems(items, (dialog, which) -> {
                    if (which == 0) checkCameraPermission();
                    else galleryLauncher.launch("image/*");
                }).show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            tempImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            cameraLauncher.launch(tempImageUri);
        } catch (IOException e) {
            Log.e("CAMERA", "Error creating image file", e);
            Toast.makeText(this, "Lỗi tạo file ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
