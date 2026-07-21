package com.example.taphoabayphuoc.repository;

import android.content.Context;

import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.database.ProductDao;
import com.example.taphoabayphuoc.models.Product;

import java.util.List;

public class ProductRepository {

    private final ProductDao productDao;

    public ProductRepository(Context context) {
        productDao = DatabaseClient
                .getInstance(context)
                .productDao();
    }

    public long insert(Product product) {
        return productDao.insert(product);
    }

    public void update(Product product) {
        productDao.update(product);
    }

    public void delete(Product product) {
        productDao.delete(product);
    }

    public Product findByBarcode(String barcode) {
        return productDao.findByBarcode(barcode);
    }

    public List<Product> getAllProducts() {
        return productDao.getAll();
    }
}