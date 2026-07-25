package com.example.taphoabayphuoc.activities.product;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
    private Uri imageUri;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String path = FileUtils.saveImageToInternal(this, uri);
                    if (path != null) {
                        imageUri = Uri.fromFile(new File(path));
                        Glide.with(this).load(imageUri).placeholder(R.drawable.ic_product).into(binding.imgProduct);
                    }
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    String path = FileUtils.saveImageToInternal(this, imageUri);
                    if (path != null) {
                        imageUri = Uri.fromFile(new File(path));
                        Glide.with(this).load(imageUri).placeholder(R.drawable.ic_product).into(binding.imgProduct);
                    }
                } else {
                    Log.e("CAMERA", "TakePicture failed or cancelled");
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền Camera để chụp ảnh", Toast.LENGTH_SHORT).show();
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
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
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
            Uri currentUri = Uri.parse(product.getImageUrl());
            Glide.with(this)
                    .load(currentUri)
                    .placeholder(R.drawable.ic_product)
                    .into(binding.imgProduct);
        }
    }

    private void updateProduct() {
        String name = binding.edtName.getText().toString().trim();
        String barcode = binding.edtBarcode.getText().toString().trim();
        String importPriceText = binding.edtImportPrice.getText().toString().trim();
        String sellPriceText = binding.edtSellPrice.getText().toString().trim();
        String quantityText = binding.edtQuantity.getText().toString().trim();

        if (name.isEmpty()) {
            binding.edtName.setError("Nhập tên sản phẩm");
            binding.edtName.requestFocus();
            return;
        }

        if (sellPriceText.isEmpty()) {
            binding.edtSellPrice.setError("Nhập giá bán");
            binding.edtSellPrice.requestFocus();
            return;
        }

        if (!barcode.isEmpty()) {
            Product check = repository.getByBarcode(barcode);
            if (check != null && check.getId() != product.getId()) {
                binding.edtBarcode.setError("Barcode đã tồn tại");
                binding.edtBarcode.requestFocus();
                return;
            }
        }

        double importPrice;
        double sellPrice;
        int quantity;

        try {
            importPrice = importPriceText.isEmpty() ? 0 : Double.parseDouble(importPriceText);
            sellPrice = Double.parseDouble(sellPriceText);
            quantity = quantityText.isEmpty() ? 0 : Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá hoặc số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        product.setName(name);
        product.setBarcode(barcode);
        product.setImportPrice(importPrice);
        product.setSellPrice(sellPrice);
        product.setQuantity(quantity);
        product.setActive(binding.chkActive.isChecked());
        
        if (imageUri != null) {
            product.setImageUrl(imageUri.toString());
        }

        repository.update(product);
        Toast.makeText(this, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showImageDialog() {
        String[] items = {"📷 Chụp ảnh", "🖼 Chọn từ thư viện"};
        new AlertDialog.Builder(this)
                .setTitle("Ảnh sản phẩm")
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        galleryLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            cameraLauncher.launch(imageUri);
        } catch (IOException e) {
            Log.e("CAMERA", "Error creating image file", e);
            Toast.makeText(this, "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
