package com.example.taphoabayphuoc.activities.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taphoabayphuoc.adapter.ProductAdapter;
import com.example.taphoabayphuoc.databinding.ActivityProductBinding;
import com.example.taphoabayphuoc.listener.ProductListener;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductRepository;

import java.util.List;

public class ProductActivity extends AppCompatActivity implements ProductListener {

    private ActivityProductBinding binding;
    private ProductRepository repository;
    private ProductAdapter adapter;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductBinding.inflate(getLayoutInflater());
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

        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.swipeRefresh.setOnRefreshListener(this::syncProducts);
    }

    private void initEvent() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchProduct(s.toString());
            }
        });

        binding.fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(ProductActivity.this, AddProductActivity.class));
        });
    }

    private void syncProducts() {
        binding.swipeRefresh.setRefreshing(true);

        // Timeout to stop spinner after 10 seconds
        binding.swipeRefresh.postDelayed(() -> {
            if (binding.swipeRefresh.isRefreshing()) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(ProductActivity.this, "Đồng bộ quá lâu, vui lòng kiểm tra kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        }, 10000);

        repository.syncProductsFromFirebase(new ProductRepository.SyncCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    if (binding.swipeRefresh.isRefreshing()) {
                        binding.swipeRefresh.setRefreshing(false);
                        loadProductsDirectly();
                        Toast.makeText(ProductActivity.this, "Đã cập nhật dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    if (binding.swipeRefresh.isRefreshing()) {
                        binding.swipeRefresh.setRefreshing(false);
                        Toast.makeText(ProductActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadProducts() {
        List<Product> list = repository.getAllProducts();
        Log.d("ROOM", "Product count = " + list.size());
        
        // Only trigger auto-sync if list is empty and it's the first time opening the screen
        if (list.isEmpty() && isFirstLoad) {
            isFirstLoad = false;
            syncProducts();
        }
        
        adapter = new ProductAdapter(list, this);
        binding.rvProducts.setAdapter(adapter);
    }

    private void loadProductsDirectly() {
        List<Product> list = repository.getAllProducts();
        if (adapter != null) {
            adapter.setProducts(list);
        } else {
            adapter = new ProductAdapter(list, this);
            binding.rvProducts.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsDirectly();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onEdit(Product product) {
        Intent intent = new Intent(this, EditProductActivity.class);
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
                    loadProductsDirectly();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void searchProduct(String keyword) {
        List<Product> list;
        if (keyword.trim().isEmpty()) {
            list = repository.getAllProducts();
        } else {
            list = repository.search(keyword);
        }
        if (adapter != null) {
            adapter.setProducts(list);
        }
    }
}
