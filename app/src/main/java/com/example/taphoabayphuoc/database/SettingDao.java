package com.example.taphoabayphuoc.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taphoabayphuoc.models.SettingEntity;

@Dao
public interface SettingDao {

    @Insert
    void insert(SettingEntity setting);

    @Update
    void update(SettingEntity setting);

    @Query("SELECT * FROM settings LIMIT 1")
    SettingEntity get();
    @Query("SELECT * FROM settings LIMIT 1")
    SettingEntity getSetting();
}