package com.example.taphoabayphuoc.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taphoabayphuoc.models.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(UserEntity user);

    @Query("SELECT * FROM users")
    List<UserEntity> getAll();

    @Query("SELECT * FROM users WHERE username=:username LIMIT 1")
    UserEntity getByUsername(String username);

}