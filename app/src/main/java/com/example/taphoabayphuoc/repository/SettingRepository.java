package com.example.taphoabayphuoc.repository;

import android.content.Context;

import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.database.SettingDao;
import com.example.taphoabayphuoc.models.SettingEntity;

public class SettingRepository {

    private final SettingDao dao;

    public SettingRepository(Context context) {

        dao = DatabaseClient
                .getInstance(context)
                .settingDao();

    }

    public SettingEntity get(){

        return dao.get();

    }

    public void insert(SettingEntity setting){

        dao.insert(setting);

    }

    public void update(SettingEntity setting){

        dao.update(setting);

    }

    public SettingEntity getSetting() {


        SettingEntity setting = dao.getSetting();

        if (setting == null) {

            setting = new SettingEntity();

            dao.insert(setting);

        }

        return setting;

    }

}