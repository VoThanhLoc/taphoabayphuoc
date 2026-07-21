package com.example.taphoabayphuoc.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static AppDatabase database;

    public static AppDatabase getInstance(Context context){

        if(database==null){

            database = Room.databaseBuilder(
                            context,
                            AppDatabase.class,
                            "taphoa.db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

        }

        return database;
    }

}