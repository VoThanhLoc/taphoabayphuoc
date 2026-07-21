package com.example.taphoabayphuoc.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.taphoabayphuoc.models.Product;

@Database(
        entities = {Product.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductDao productDao();

}