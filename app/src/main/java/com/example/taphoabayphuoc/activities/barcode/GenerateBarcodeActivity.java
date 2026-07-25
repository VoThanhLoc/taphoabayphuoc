package com.example.taphoabayphuoc.activities.barcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.databinding.ActivityGenerateBarcodeBinding;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductRepository;
import com.example.taphoabayphuoc.utils.BarcodeGenerator;

import java.util.ArrayList;
import java.util.List;

public class GenerateBarcodeActivity extends AppCompatActivity {

    private ActivityGenerateBarcodeBinding binding;
    private ProductRepository repository;
    private List<Product> productList;
    private ArrayAdapter<String> productAdapter;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGenerateBarcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new ProductRepository(getApplicationContext());

        initView();
        initEvent();
        loadProducts();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.btnPrint.setEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void initEvent() {

        binding.actProduct.setOnItemClickListener((parent, view, position, id) -> {

            selectedProduct = productList.get(position);

            if (selectedProduct.getBarcode() != null &&
                    !selectedProduct.getBarcode().isEmpty()) {

                String barcode = selectedProduct.getBarcode();
                binding.txtBarcode.setText(barcode);

                // Hiển thị hình barcode
                displayBarcodeImage(barcode);

                binding.btnPrint.setEnabled(true);

            } else {

                binding.txtBarcode.setText("Chưa có mã vạch");

                binding.imgBarcode.setImageDrawable(null);

                binding.btnPrint.setEnabled(false);
            }
        });

        binding.btnGenerate.setOnClickListener(v -> {

            if (selectedProduct == null) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            String barcode = repository.generateUniqueBarcode();

            binding.txtBarcode.setText(barcode);

            displayBarcodeImage(barcode);

            binding.btnPrint.setEnabled(true);
        });
    }

    private void displayBarcodeImage(String barcode) {
        if (barcode.length() == 13) {
            Bitmap bitmap = BarcodeGenerator.createBarcodeBitmap(barcode, 600, 300);
            if (bitmap != null) {
                binding.imgBarcode.setImageBitmap(bitmap);
            }
        }
    }
    private void loadProducts() {

        productList = repository.getAllProducts();

        List<String> names = new ArrayList<>();

        for (Product product : productList) {
            names.add(product.getName());
        }

        productAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                names
        );

        binding.actProduct.setAdapter(productAdapter);
    }

}