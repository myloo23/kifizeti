package com.example.kifizeti_android.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.kifizeti_android.data.dao.EventDao;
import com.example.kifizeti_android.data.entity.Event;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}