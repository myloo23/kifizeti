package com.example.kifizeti_android.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * Az eseményeket reprezentáló entitás osztály.
 * Ez az osztály felel a helyi Room adatbázis táblájáért
 * és a Supabase felhő JSON adatainak leképezéséért is.
 */
@Entity(tableName = "events")
public class Event {


    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private long createdAt;


    @SerializedName("category")
    private String category;


    public Event(String name, String description, long createdAt, String category) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.category = category;
    }

    // --- Getter és Setter metódusok ---
    // Ezek szükségesek a Room és a GSON könyvtárak működéséhez.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}