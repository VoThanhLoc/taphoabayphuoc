package com.example.taphoabayphuoc.activities.product;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.taphoabayphuoc.R;
import com.example.taphoabayphuoc.databinding.ActivityEditProductBinding;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductFirebaseRepository;
import com.example.taphoabayphuoc.repository.ProductRepository;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;
public class EditProductActivity extends AppCompatActivity {

    private ActivityEditProductBinding binding;

    private ProductRepository repository;
    private Product product;

    private Uri imageUri;
    private ProductFirebaseRepository firebaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseRepository = new ProductFirebaseRepository();
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new ProductRepository(getApplicationContext());

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int id = getIntent().getIntExtra("id", -1);

        product = repository.getById(id);

        if (product == null) {
            finish();
            return;
        }

        loadData();

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

            imageUri = Uri.parse(product.getImageUrl());

            Glide.with(this)
                    .load(imageUri)
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

        if (barcode.isEmpty()) {
            binding.edtBarcode.setError("Nhập barcode");
            binding.edtBarcode.requestFocus();
            return;
        }

        Product check = repository.getByBarcode(barcode);

        if (check != null && check.getId() != product.getId()) {

            binding.edtBarcode.setError("Barcode đã tồn tại");
            binding.edtBarcode.requestFocus();
            return;
        }

        if (importPriceText.isEmpty()) {
            binding.edtImportPrice.setError("Nhập giá nhập");
            binding.edtImportPrice.requestFocus();
            return;
        }

        if (sellPriceText.isEmpty()) {
            binding.edtSellPrice.setError("Nhập giá bán");
            binding.edtSellPrice.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            binding.edtQuantity.setError("Nhập số lượng");
            binding.edtQuantity.requestFocus();
            return;
        }

        double importPrice;
        double sellPrice;
        int quantity;

        try {

            importPrice = Double.parseDouble(importPriceText);
            sellPrice = Double.parseDouble(sellPriceText);
            quantity = Integer.parseInt(quantityText);

        } catch (NumberFormatException e) {

            Toast.makeText(this,
                    "Giá hoặc số lượng không hợp lệ",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        product.setName(name);
        product.setBarcode(barcode);
        product.setImportPrice(importPrice);
        product.setSellPrice(sellPrice);
        product.setQuantity(quantity);
        product.setActive(binding.chkActive.isChecked());
        product.setImageUrl(imageUri == null ? "" : imageUri.toString());

        repository.update(product);
        firebaseRepository.update(product);
        Toast.makeText(this,
                "Đã cập nhật sản phẩm",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            imageUri = uri;

                            Glide.with(this)
                                    .load(uri)
                                    .placeholder(R.drawable.ic_product)
                                    .into(binding.imgProduct);

                        }

                    });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {

                        if (success) {

                            Glide.with(this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_product)
                                    .into(binding.imgProduct);

                        }

                    });
    private void showImageDialog() {

        String[] items = {
                "📷 Chụp ảnh",
                "🖼 Chọn từ thư viện"
        };

        new AlertDialog.Builder(this)
                .setTitle("Ảnh sản phẩm")
                .setItems(items, (dialog, which) -> {

                    if (which == 0) {

                        openCamera();

                    } else {

                        galleryLauncher.launch("image/*");

                    }

                })
                .show();

    }
    private void openCamera() {

        try {

            File imageFile = File.createTempFile(
                    "product_",
                    ".jpg",
                    getCacheDir()
            );

            imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    imageFile
            );

            cameraLauncher.launch(imageUri);

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Không thể mở Camera",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }
}