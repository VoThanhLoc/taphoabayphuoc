package com.example.taphoabayphuoc.models;

public class InvoiceItem {

    private Product product;
    private int quantity;
    private double price;
    private boolean isWholesale;

    public InvoiceItem() {
    }

    public InvoiceItem(Product product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.isWholesale = false;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public boolean isWholesale() {
        return isWholesale;
    }

    public void setWholesale(boolean wholesale) {
        isWholesale = wholesale;
    }

    public double getDisplayPrice() {
        return isWholesale ? price * 10 : price;
    }

    public double getTotal() {
        return quantity * getDisplayPrice();
    }
}
