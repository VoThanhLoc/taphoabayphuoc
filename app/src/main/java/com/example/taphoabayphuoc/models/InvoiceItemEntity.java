package com.example.taphoabayphuoc.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoice_item")
public class InvoiceItemEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int invoiceId;

    private int productId;

    private String barcode;

    private String productName;

    private int quantity;

    private double price;

    private double total;

    public InvoiceItemEntity() {
    }

    public InvoiceItemEntity(int id,
                             int invoiceId,
                             int productId,
                             String barcode,
                             String productName,
                             int quantity,
                             double price,
                             double total) {

        this.id = id;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}