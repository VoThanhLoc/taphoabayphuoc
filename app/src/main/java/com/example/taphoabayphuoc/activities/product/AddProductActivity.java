package com.example.taphoabayphuoc.activities.product;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.taphoabayphuoc.R;
import com.example.taphoabayphuoc.databinding.ActivityAddProductBinding;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductFirebaseRepository;
import com.example.taphoabayphuoc.repository.ProductRepository;

import java.io.File;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;

    private ProductRepository repository;

    private Uri imageUri;
    private ProductFirebaseRepository firebaseRepository;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        firebaseRepository = new ProductFirebaseRepository();
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        repository = new ProductRepository(this);

        binding.btnChooseImage.setOnClickListener(v -> showImageDialog());

        binding.btnSave.setOnClickListener(v -> saveProduct());

    }

    private void saveProduct() {

        String barcode = binding.edtBarcode.getText().toString().trim();
        String name = binding.edtName.getText().toString().trim();
        String importPriceText = binding.edtImportPrice.getText().toString().trim();
        String sellPriceText = binding.edtSellPrice.getText().toString().trim();
        String quantityText = binding.edtQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(barcode)) {
            binding.edtBarcode.setError("Vui lòng nhập mã vạch");
            binding.edtBarcode.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            binding.edtName.setError("Vui lòng nhập tên sản phẩm");
            binding.edtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(importPriceText)) {
            binding.edtImportPrice.setError("Vui lòng nhập giá nhập");
            binding.edtImportPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(sellPriceText)) {
            binding.edtSellPrice.setError("Vui lòng nhập giá bán");
            binding.edtSellPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityText)) {
            binding.edtQuantity.setError("Vui lòng nhập số lượng");
            binding.edtQuantity.requestFocus();
            return;
        }

        Product existed = repository.getByBarcode(barcode);

        if (existed != null) {

            binding.edtBarcode.setError("Mã vạch đã tồn tại");
            binding.edtBarcode.requestFocus();

            return;

        }

        double importPrice;
        double sellPrice;
        int quantity;

        try {

            importPrice = Double.parseDouble(importPriceText);
            sellPrice = Double.parseDouble(sellPriceText);
            quantity = Integer.parseInt(quantityText);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Dữ liệu không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        Product product = new Product();

        product.setBarcode(barcode);
        product.setName(name);
        product.setImportPrice(importPrice);
        product.setSellPrice(sellPrice);
        product.setQuantity(quantity);
        product.setActive(true);

        if (imageUri != null) {

            product.setImageUrl(imageUri.toString());

        } else {

            product.setImageUrl("");

        }
        firebaseRepository.insert(product);
        repository.insert(product);

        Toast.makeText(
                this,
                "Thêm sản phẩm thành công",
                Toast.LENGTH_SHORT
        ).show();

        finish();

    }

    private void showImageDialog() {

        String[] items = {
                "📷 Chụp ảnh",
                "🖼 Chọn từ thư viện"
        };

        new AlertDialog.Builder(this)
                .setTitle("Chọn ảnh sản phẩm")
                .setItems(items, (dialog, which) -> {

                    switch (which) {

                        case 0:
                            openCamera();
                            break;

                        case 1:
                            galleryLauncher.launch("image/*");
                            break;

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

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }
}