package com.example.taphoabayphuoc.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.taphoabayphuoc.dao.InvoiceDao;
import com.example.taphoabayphuoc.dao.InvoiceItemDao;
import com.example.taphoabayphuoc.database.ProductDao;
import com.example.taphoabayphuoc.models.InvoiceEntity;
import com.example.taphoabayphuoc.models.InvoiceItemEntity;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.models.SettingEntity;
import com.example.taphoabayphuoc.models.UserEntity;

@Database(
        entities = {
                Product.class,
                InvoiceEntity.class,
                InvoiceItemEntity.class,
                UserEntity.class,
                SettingEntity.class
        },
        version = 7,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductDao productDao();

    public abstract InvoiceDao invoiceDao();

    public abstract InvoiceItemDao invoiceItemDao();
    public abstract UserDao userDao();

    public abstract SettingDao settingDao();

}