package com.example.kifizeti_android.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * Az Expense entitás, amely mind a helyi Room adatbázis,
 * mind a távoli Supabase tábla szerkezetét reprezentálja.
 */
@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") // Supabase-ben 'id'
    private Long id; // int-ről Long-ra módosítva a bigint kompatibilitás miatt

    @SerializedName("event_id") // KRITIKUS: A Supabase oszlop neve 'event_id'
    private long eventId;

    @SerializedName("description")
    private String description;

    @SerializedName("amount")
    private double amount;

    @SerializedName("payer")
    private String payer;

    @SerializedName("participants")
    private String participants;

    /**
     * Üres konstruktor a Room és a GSON számára.
     */
    public Expense() {
    }

    /**
     * Konstruktor az új kiadások létrehozásához.
     */
    public Expense(long eventId, String description, double amount, String payer, String participants) {
        this.eventId = eventId;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
    }

    // --- Getterek és Setterek ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }
}