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
}