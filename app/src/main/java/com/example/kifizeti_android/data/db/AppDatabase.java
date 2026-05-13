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

/**
 * Az alkalmazás központi Room adatbázis osztálya.
 * Ez az osztály felelős a technikai adatbázis-kapcsolat kezeléséért és a DAO-k elérhetővé tételéért.
 */
@Database(entities = {Event.class, User.class, Expense.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Singleton példány az erőforrás-pazarlás elkerülése érdekében
    private static volatile AppDatabase INSTANCE;

    // Az adatelérési objektumok (DAO) absztrakt metódusai
    public abstract EventDao eventDao();
    public abstract UserDao userDao();
    public abstract ExpenseDao expenseDao();

    /**
     * Visszaadja az adatbázis egyetlen példányát. Ha még nem létezik, létrehozza azt.
     *
     * @param context Az alkalmazás kontextusa
     * @return Az AppDatabase példánya
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "kifizeti_database_v2")
                            // Ha a verziószám nő, és nincs migrációs terv, törli a régi adatokat és újrakezdi
                            .fallbackToDestructiveMigration()

                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}