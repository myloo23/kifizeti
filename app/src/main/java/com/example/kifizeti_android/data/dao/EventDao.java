package com.example.kifizeti_android.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kifizeti_android.data.entity.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    void insert(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    List<Event> getAllEvents();

    @Update
    void update(Event event);

    @Query("SELECT * FROM events WHERE name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    List<Event> searchEvents(String query);

    @Query("SELECT * FROM events ORDER BY name ASC")
    List<Event> getAllEventsByName();

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    List<Event> getAllEventsByDate();

    @Query("SELECT * FROM events WHERE name = :name LIMIT 1")
    Event getEventByExactName(String name);
}