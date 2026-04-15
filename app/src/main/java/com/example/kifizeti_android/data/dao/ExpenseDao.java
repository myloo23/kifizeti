package com.example.kifizeti_android.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kifizeti_android.data.entity.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses WHERE eventId = :eventId")
    List<Expense> getExpensesForEvent(int eventId);

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    Expense getExpenseById(int expenseId);
}
