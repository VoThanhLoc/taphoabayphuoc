package com.example.taphoabayphuoc.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taphoabayphuoc.models.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products ORDER BY name")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE barcode=:barcode LIMIT 1")
    Product findByBarcode(String barcode);

    @Query("SELECT COUNT(*) FROM products")
    int getCount();

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    Product getByBarcode(String barcode);

    @Query("SELECT * FROM products WHERE id=:id LIMIT 1")
    Product getById(int id);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%' OR barcode LIKE '%' || :keyword || '%' ORDER BY name")
    List<Product> search(String keyword);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    @Query("DELETE FROM products")
    void deleteAll();

    @Query("SELECT * FROM products WHERE firebaseId=:firebaseId LIMIT 1")
    Product findByFirebaseId(String firebaseId);
}