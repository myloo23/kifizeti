package com.example.kifizeti_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final List<Expense> expenses;
    private final Context context;
    private final AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ExpenseAdapter(Context context, List<Expense> expenses, int eventId) {
        this.context = context;
        this.expenses = expenses;
        this.db = AppDatabase.getDatabase(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvAmount, tvPayer, tvParticipants;
        Button btnEdit, btnDelete;

        public ViewHolder(View view) {
            super(view);
            tvDescription = view.findViewById(R.id.tvExpenseDescription);
            tvAmount = view.findViewById(R.id.tvExpenseAmount);
            tvPayer = view.findViewById(R.id.tvExpensePayer);
            tvParticipants = view.findViewById(R.id.tvExpenseParticipants);
            btnEdit = view.findViewById(R.id.btnEditExpense);
            btnDelete = view.findViewById(R.id.btnDeleteExpense);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.tvDescription.setText(expense.getDescription());
        holder.tvAmount.setText(String.format("%.0f Ft", expense.getAmount()));
        holder.tvPayer.setText("Fizette: " + expense.getPayer());
        holder.tvParticipants.setText("Résztvevők: " + expense.getParticipants());

        holder.btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("event_id", expense.getEventId());
            bundle.putInt("expense_id", expense.getId());
            Navigation.findNavController(v).navigate(R.id.nav_add_expense, bundle);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Kiadás törlése")
                    .setMessage("Biztosan törölni szeretnéd ezt a kiadást?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        executorService.execute(() -> {
                            db.expenseDao().delete(expense);
                            if (context instanceof AppCompatActivity) {
                                ((AppCompatActivity) context).runOnUiThread(() -> {
                                    int pos = holder.getAdapterPosition();
                                    if (pos != RecyclerView.NO_POSITION) {
                                        expenses.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                });
                            }
                        });
                    })
                    .setNegativeButton("Mégse", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }
}
