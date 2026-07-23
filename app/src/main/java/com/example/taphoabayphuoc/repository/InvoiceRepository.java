package com.example.taphoabayphuoc.repository;

import android.content.Context;

import com.example.taphoabayphuoc.database.AppDatabase;
import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.models.Invoice;
import com.example.taphoabayphuoc.models.InvoiceEntity;
import com.example.taphoabayphuoc.models.InvoiceItem;
import com.example.taphoabayphuoc.models.InvoiceItemEntity;

public class InvoiceRepository {

    private final AppDatabase db;

    public InvoiceRepository(Context context) {
        db = DatabaseClient.getInstance(context);
    }

    public void saveInvoice(Invoice invoice) {

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
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            detail.setTotal(item.getTotal());

            db.invoiceItemDao().insert(detail);
        }
    }
}