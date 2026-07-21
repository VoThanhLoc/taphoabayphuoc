package com.example.taphoabayphuoc.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice {

    private String id;

    private Date createdDate;

    private List<InvoiceItem> items = new ArrayList<>();

    private double total;

    public Invoice() {
    }

    public Invoice(String id, Date createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    public void calculateTotal() {

        total = 0;

        for (InvoiceItem item : items) {
            total += item.getTotal();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }
}