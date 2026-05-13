package com.example.kifizeti_android.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kifizeti_android.data.entity.Event;

import java.util.List;

@Dao
public interface EventDao {

    /**
     * Beszúr egy eseményt. Ha az ID már létezik, felülírja a meglévőt.
     * Ez biztosítja, hogy a Supabase-ből érkező friss adatok
     * ne okozzanak ütközést.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Delete
    void delete(Event event);

    @Update
    void update(Event event);

    // Lekérdezések

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    List<Event> getAllEventsByDate();

    @Query("SELECT * FROM events ORDER BY name ASC")
    List<Event> getAllEventsByName();

    /**
     * Keresés a név alapján.
     */
    @Query("SELECT * FROM events WHERE name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    List<Event> searchEvents(String query);

    /**
     * Pontos név alapján való keresés (pl. duplikáció ellenőrzéshez).
     */
    @Query("SELECT * FROM events WHERE name = :name LIMIT 1")
    Event getEventByExactName(String name);
}