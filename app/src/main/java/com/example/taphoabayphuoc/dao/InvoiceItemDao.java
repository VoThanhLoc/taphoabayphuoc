package com.example.taphoabayphuoc.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taphoabayphuoc.models.InvoiceItemEntity;

import java.util.List;

@Dao
public interface InvoiceItemDao {

    @Insert
    void insert(InvoiceItemEntity item);

    @Query("SELECT * FROM invoice_item WHERE invoiceId=:invoiceId")
    List<InvoiceItemEntity> getByInvoice(int invoiceId);
    @Query("SELECT * FROM invoice_item WHERE invoiceId=:invoiceId")
    List<InvoiceItemEntity> getByInvoiceId(int invoiceId);
}