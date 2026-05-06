package com.example.kifizeti_android.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String name;
    private final String description;
    private final long createdAt;
    private final String category;

    public Event(String name, String description, long createdAt, String category) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCategory() {
        return category;
    }
}