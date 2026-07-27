package com.example.taphoabayphuoc.repository;

import android.content.Context;

import com.example.taphoabayphuoc.database.AppDatabase;
import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.models.Invoice;
import com.example.taphoabayphuoc.models.InvoiceEntity;
import com.example.taphoabayphuoc.models.InvoiceItem;
import com.example.taphoabayphuoc.models.InvoiceItemEntity;
import com.example.taphoabayphuoc.firebase.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.List;

public class InvoiceRepository {

    private final AppDatabase db;

    public InvoiceRepository(Context context) {
        db = DatabaseClient.getInstance(context.getApplicationContext());
    }

    public interface SyncCallback {
        void onSuccess();
        void onError(String message);
    }

    public void saveInvoice(Invoice invoice) {
// ... existing saveInvoice content ...

        // 1. Save to local SQLite
        InvoiceEntity entity = new InvoiceEntity();

        entity.setCode(invoice.getId());
        entity.setCreatedDate(invoice.getCreatedDate().getTime());
        entity.setTotal(invoice.getTotal());

        long invoiceId = db.invoiceDao().insert(entity);

        for (InvoiceItem item : invoice.getItems()) {

            InvoiceItemEntity detail = new InvoiceItemEntity();

            detail.setInvoiceId((int) invoiceId);
            detail.setProductId(item.getProduct().getId());
            detail.setBarcode(item.getProduct().getBarcode());
            detail.setProductName(item.getProduct().getName());
            detail.setUnit(item.getProduct().getUnit());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            detail.setTotal(item.getTotal());

            db.invoiceItemDao().insert(detail);
        }

        // 2. Save to Firebase
        saveToFirebase(invoice);
    }

    private void saveToFirebase(Invoice invoice) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference ref = FirebaseManager.getDatabase()
                .getReference("users")
                .child(uid)
                .child("invoices");

        ref.child(invoice.getId()).setValue(invoice);
    }

    public void syncInvoicesFromFirebase(SyncCallback callback) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            callback.onError("User not logged in");
            return;
        }

        DatabaseReference ref = FirebaseManager.getDatabase()
                .getReference("users")
                .child(uid)
                .child("invoices");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Invoice firebaseInvoice = child.getValue(Invoice.class);
                        if (firebaseInvoice == null) continue;

                        InvoiceEntity existing = db.invoiceDao().findByCode(firebaseInvoice.getId());
                        if (existing == null) {
                            // Insert new invoice
                            InvoiceEntity entity = new InvoiceEntity();
                            entity.setCode(firebaseInvoice.getId());
                            entity.setCreatedDate(firebaseInvoice.getCreatedDate().getTime());
                            entity.setTotal(firebaseInvoice.getTotal());

                            long invoiceId = db.invoiceDao().insert(entity);

                            for (InvoiceItem item : firebaseInvoice.getItems()) {
                                InvoiceItemEntity detail = new InvoiceItemEntity();
                                detail.setInvoiceId((int) invoiceId);
                                if (item.getProduct() != null) {
                                    detail.setProductId(item.getProduct().getId());
                                    detail.setBarcode(item.getProduct().getBarcode());
                                    detail.setProductName(item.getProduct().getName());
                                    detail.setUnit(item.getProduct().getUnit());
                                }
                                detail.setQuantity(item.getQuantity());
                                detail.setPrice(item.getPrice());
                                detail.setTotal(item.getTotal());

                                db.invoiceItemDao().insert(detail);
                            }
                        }
                    }
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}
