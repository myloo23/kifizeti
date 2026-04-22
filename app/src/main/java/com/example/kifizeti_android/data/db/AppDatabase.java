package com.example.kifizeti_android.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.kifizeti_android.data.dao.EventDao;
import com.example.kifizeti_android.data.dao.ExpenseDao;
import com.example.kifizeti_android.data.dao.UserDao;
import com.example.kifizeti_android.data.entity.Event;
import com.example.kifizeti_android.data.entity.Expense;
import com.example.kifizeti_android.data.entity.User;

@Database(entities = {Event.class, User.class, Expense.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract EventDao eventDao();
    public abstract UserDao userDao();
    public abstract ExpenseDao expenseDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "kifizeti_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
