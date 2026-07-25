package com.example.taphoabayphuoc.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taphoabayphuoc.models.InvoiceEntity;

import java.util.List;

@Dao
public interface InvoiceDao {

    @Insert
    long insert(InvoiceEntity invoice);

    @Query("SELECT * FROM invoice ORDER BY id DESC")
    List<InvoiceEntity> getAll();
    @Query("SELECT * FROM invoice WHERE id=:id")
    InvoiceEntity getById(int id);

    @Query("SELECT * FROM invoice WHERE code=:code LIMIT 1")
    InvoiceEntity findByCode(String code);

    @Query("DELETE FROM invoice")
    void deleteAll();
}