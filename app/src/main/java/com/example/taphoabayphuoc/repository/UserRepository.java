package com.example.taphoabayphuoc.repository;

import android.content.Context;

import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.database.UserDao;
import com.example.taphoabayphuoc.models.UserEntity;

import java.util.List;

public class UserRepository {

    private final UserDao dao;

    public UserRepository(Context context) {

        dao = DatabaseClient
                .getInstance(context)
                .userDao();

    }

    public void insert(UserEntity user){

        dao.insert(user);

    }

    public List<UserEntity> getAll(){

        return dao.getAll();

    }

    public UserEntity getByUsername(String username){

        return dao.getByUsername(username);

    }

}