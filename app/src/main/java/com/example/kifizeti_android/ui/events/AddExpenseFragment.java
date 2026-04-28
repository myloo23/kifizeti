package com.example.kifizeti_android.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Expense;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddExpenseFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_EXPENSE_ID = "expense_id";

    private int eventId;
    private int expenseId = -1;
    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private EditText etDesc, etAmount, etPayer, etParticipants;
    private Button btnSave;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
            expenseId = getArguments().getInt(ARG_EXPENSE_ID, -1);
        }
        db = AppDatabase.getDatabase(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        etDesc = view.findViewById(R.id.etExpenseDesc);
        etAmount = view.findViewById(R.id.etExpenseAmount);
        etPayer = view.findViewById(R.id.etExpensePayer);
        etParticipants = view.findViewById(R.id.etExpenseParticipants);
        btnSave = view.findViewById(R.id.btnSaveExpense);
        Button btnCancel = view.findViewById(R.id.btnCancelExpense);

        if (expenseId != -1) {
            loadExpenseData();
        }

        btnSave.setOnClickListener(v -> saveExpense(v));
        btnCancel.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }

    private void loadExpenseData() {
        executorService.execute(() -> {
            Expense expense = db.expenseDao().getExpenseById(expenseId);
            if (expense != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    etDesc.setText(expense.getDescription());
                    etAmount.setText(String.valueOf((int)expense.getAmount()));
                    etPayer.setText(expense.getPayer());
                    etParticipants.setText(expense.getParticipants());
                });
            }
        });
    }

    private void saveExpense(View view) {
        String desc = etDesc.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String payer = etPayer.getText().toString().trim();
        String participants = etParticipants.getText().toString().trim();

        if (desc.isEmpty() || amountStr.isEmpty() || payer.isEmpty() || participants.isEmpty()) {
            Toast.makeText(getContext(), "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Érvénytelen összeg!", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            if (expenseId == -1) {
                Expense newExpense = new Expense(eventId, desc, amount, payer, participants);
                db.expenseDao().insert(newExpense);
            } else {
                Expense existing = db.expenseDao().getExpenseById(expenseId);
                if (existing != null) {
                    existing.setDescription(desc);
                    existing.setAmount(amount);
                    existing.setPayer(payer);
                    existing.setParticipants(participants);
                    db.expenseDao().update(existing);
                }
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Sikeres mentés!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).popBackStack();
                });
            }
        });
    }
}
