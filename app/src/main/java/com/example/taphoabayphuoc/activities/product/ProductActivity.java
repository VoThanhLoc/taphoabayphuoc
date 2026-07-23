package com.example.taphoabayphuoc.activities.product;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taphoabayphuoc.adapter.ProductAdapter;
import com.example.taphoabayphuoc.databinding.ActivityProductBinding;
import com.example.taphoabayphuoc.listener.ProductListener;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductRepository;

import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class ProductActivity extends AppCompatActivity implements ProductListener {

    private ActivityProductBinding binding;

    private ProductRepository repository;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                searchProduct(s.toString());
            }
        });
        repository = new ProductRepository(getApplicationContext());

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.rvProducts.setLayoutManager(
                new LinearLayoutManager(this));

        loadProducts();

        binding.fabAdd.setOnClickListener(v -> {

            startActivity(new Intent(
                    ProductActivity.this,
                    AddProductActivity.class));

        });

    }

    private void loadProducts() {

        List<Product> list = repository.getAllProducts();
        Log.d("ROOM", "Product count = " + list.size());
        adapter = new ProductAdapter(list, this);
        binding.rvProducts.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadProducts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onEdit(Product product) {

        Intent intent = new Intent(
                this,
                EditProductActivity.class);

        intent.putExtra("id", product.getId());

        startActivity(intent);

    }

    @Override
    public void onDelete(Product product) {

        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa?")

                .setPositiveButton("Xóa", (dialog, which) -> {

                    repository.delete(product);

                    loadProducts();

                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void searchProduct(String keyword){
        List<Product> list;
        if(keyword.trim().isEmpty()){
            list = repository.getAllProducts();
        }else{
            list = repository.search(keyword);
        }
        adapter.setProducts(list);

    }
}