package com.example.taphoabayphuoc.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class SettingEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String shopName;

    private String address;

    private String phone;

    private String printerName;

    private String printerMac;

    public SettingEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getPrinterMac() {
        return printerMac;
    }

    public void setPrinterMac(String printerMac) {
        this.printerMac = printerMac;
    }
}