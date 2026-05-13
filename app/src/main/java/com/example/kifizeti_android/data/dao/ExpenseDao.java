package com.example.kifizeti_android.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kifizeti_android.data.entity.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    /**
     * Új kiadás beszúrása.
     * OnConflictStrategy.REPLACE: Ha már létezik ilyen ID-val rekord (pl. felhőből frissítéskor),
     * akkor felülírja a régit, megakadályozva az alkalmazás összeomlását.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    /**
     * Lekéri az összes kiadást, amely egy adott eseményhez (eventId) tartozik.
     * Fontos: A metódus neve pontosan meg kell egyezzen a Repository-ban használt névvel!
     */
    @Query("SELECT * FROM expenses WHERE eventId = :eventId")
    List<Expense> getExpensesByEventId(long eventId);

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    Expense getExpenseById(long expenseId);
}