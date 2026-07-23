package com.example.taphoabayphuoc.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoice")
public class InvoiceEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String code;

    private long createdDate;

    private double total;

    public InvoiceEntity() {
    }

    public InvoiceEntity(int id,
                         String code,
                         long createdDate,
                         double total) {

        this.id = id;
        this.code = code;
        this.createdDate = createdDate;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}