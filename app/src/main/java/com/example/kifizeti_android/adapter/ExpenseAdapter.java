package com.example.kifizeti_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Expense;
import com.example.kifizeti_android.ui.events.AddExpenseFragment;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final List<Expense> expenses;
    private final Context context;
    private final AppDatabase db;
    private final int eventId;

    public ExpenseAdapter(Context context, List<Expense> expenses, int eventId) {
        this.context = context;
        this.expenses = expenses;
        this.eventId = eventId;
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
            ((AppCompatActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            AddExpenseFragment.newInstance(expense.getEventId(), expense.getId())
                    )
                    .addToBackStack(null)
                    .commit();
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Kiadás törlése")
                    .setMessage("Biztosan törölni szeretnéd ezt a kiadást?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        db.expenseDao().delete(expense);
                        expenses.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
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
