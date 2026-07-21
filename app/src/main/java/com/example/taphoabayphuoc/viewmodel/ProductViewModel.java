package com.example.taphoabayphuoc.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository repository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
    }

    public void seedData() {
        repository.seedData();
    }

    public Product findByBarcode(String barcode) {
        return repository.findByBarcode(barcode);
    }

    public List<Product> getAllProducts() {
        return repository.getAllProducts();
    }

    public long insert(Product product) {
        return repository.insert(product);
    }

    public void update(Product product) {
        repository.update(product);
    }

    public void delete(Product product) {
        repository.delete(product);
    }
}