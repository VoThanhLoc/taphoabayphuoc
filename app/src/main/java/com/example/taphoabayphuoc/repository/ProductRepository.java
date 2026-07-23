package com.example.taphoabayphuoc.repository;

import android.content.Context;
import android.util.Log;

import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.database.ProductDao;
import com.example.taphoabayphuoc.models.Product;

import java.util.ArrayList;
import java.util.List;
import com.example.taphoabayphuoc.firebase.FirebaseRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.ArrayList;
public class ProductRepository {

    private final ProductDao productDao;
    private final FirebaseRepository firebaseRepository;
    private ProductFirebaseRepository firebase;
    public ProductRepository(Context context) {
        firebaseRepository = new FirebaseRepository();
        productDao = DatabaseClient
                .getInstance(context)
                .productDao();
    }

    public long insert(Product product) {
        firebaseRepository.insertProduct(product);
        return productDao.insert(product);
    }

    public void update(Product product) {
        firebaseRepository.updateProduct(product);
        productDao.update(product);
    }

    public void delete(Product product) {
        firebaseRepository.deleteProduct(product);
        productDao.delete(product);
    }

    public Product findByBarcode(String barcode) {
        return productDao.findByBarcode(barcode);
    }

    public List<Product> getAllProducts() {
        return productDao.getAll();
    }

    public Product getByBarcode(String barcode){
        return productDao.getByBarcode(barcode);
    }

    public Product getById(int id){

        return productDao.getById(id);

    }
    public interface SyncCallback {
        void onSuccess();
        void onError(String message);
    }

    public List<Product> search(String keyword) {
        return productDao.search(keyword);
    }

    public void syncProductsFromFirebase(SyncCallback callback) {
        Log.d("SYNC", "syncProductsFromFirebase called");
        firebaseRepository.loadProducts(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("SYNC", "Firebase count = " + snapshot.getChildrenCount());
                for (DataSnapshot child : snapshot.getChildren()) {

                    Product firebaseProduct =
                            child.getValue(Product.class);

                    if (firebaseProduct == null) {
                        continue;
                    }
                    firebaseProduct.setFirebaseId(child.getKey());
                    Product localProduct =
                            productDao.findByFirebaseId(
                                    firebaseProduct.getFirebaseId());

                    if (localProduct == null) {
                        productDao.insert(firebaseProduct);
                    } else {
                        firebaseProduct.setId(localProduct.getId());
                        productDao.update(firebaseProduct);
                    }
                }
                callback.onSuccess();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });

    }
}