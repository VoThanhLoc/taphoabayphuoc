package com.example.taphoabayphuoc.listener;

import com.example.taphoabayphuoc.models.Product;

public interface ProductListener {

    void onEdit(Product product);

    void onDelete(Product product);

}