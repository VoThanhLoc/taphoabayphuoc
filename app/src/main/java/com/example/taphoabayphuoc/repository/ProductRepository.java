package com.example.taphoabayphuoc.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.database.ProductDao;
import com.example.taphoabayphuoc.firebase.FirebaseRepository;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.utils.BarcodeGenerator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductRepository {

    private final ProductDao productDao;
    private final FirebaseRepository firebaseRepository;

    public ProductRepository(Context context) {
        firebaseRepository = new FirebaseRepository();
        productDao = DatabaseClient
                .getInstance(context.getApplicationContext())
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
                try {
                    Log.d("SYNC", "Data snapshot: " + snapshot.toString());
                    Log.d("SYNC", "Firebase count = " + snapshot.getChildrenCount());

                    if (!snapshot.exists()) {
                        Log.d("SYNC", "No products found in Firebase");
                        callback.onSuccess();
                        return;
                    }

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Log.d("SYNC", "Processing child: " + child.getKey());
                        Product firebaseProduct = child.getValue(Product.class);

                        if (firebaseProduct == null) {
                            Log.e("SYNC", "Failed to parse product for key: " + child.getKey());
                            continue;
                        }

                        firebaseProduct.setFirebaseId(child.getKey());

                        Product localProduct =
                                productDao.findByFirebaseId(firebaseProduct.getFirebaseId());

                        if (localProduct == null) {
                            productDao.insert(firebaseProduct);
                            Log.d("SYNC", "Insert: " + firebaseProduct.getName());
                        } else {
                            firebaseProduct.setId(localProduct.getId());
                            productDao.update(firebaseProduct);
                            Log.d("SYNC", "Update: " + firebaseProduct.getName());
                        }
                    }

                    callback.onSuccess();

                } catch (Exception e) {
                    Log.e("SYNC", "Exception", e);
                    callback.onError(e.getMessage());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });

    }

    public String generateUniqueBarcode() {
        while (true) {
            String barcode = BarcodeGenerator.generate();
            Product product = productDao.findByBarcode(barcode);
            if (product == null) {
                return barcode;
            }
        }
    }
}
