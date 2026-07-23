package com.example.taphoabayphuoc.models;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String firebaseId;
    private String barcode;
    private String name;
    private double importPrice;
    private double sellPrice;
    private int quantity;
    private String imageUrl;
    private boolean active;

    public Product() {
    }

    public Product(int id,
                   String firebaseId,
                   String barcode,
                   String name,
                   double importPrice,
                   double sellPrice,
                   int quantity,
                   String imageUrl,
                   boolean active) {

        this.id = id;
        this.firebaseId = firebaseId;
        this.barcode = barcode;
        this.name = name;
        this.importPrice = importPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        this.importPrice = importPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}