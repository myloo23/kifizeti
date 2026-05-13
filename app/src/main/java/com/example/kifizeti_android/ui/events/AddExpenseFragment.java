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
import com.example.kifizeti_android.data.entity.Expense;
import com.example.kifizeti_android.data.repository.EventRepository;
import com.example.kifizeti_android.data.repository.RepositoryCallback;

import java.util.List;

public class AddExpenseFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_EXPENSE_ID = "expense_id";

    private long eventId = -1L;
    private long expenseId = -1L;

    // JAVÍTÁS: ExecutorService helyett a Repository-t használjuk!
    private EventRepository eventRepository;

    private EditText etDesc, etAmount, etPayer, etParticipants;
    private Button btnSave;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong(ARG_EVENT_ID, -1L);
            expenseId = getArguments().getLong(ARG_EXPENSE_ID, -1L);
        }
        // Repository inicializálása
        eventRepository = new EventRepository(requireContext());
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

        if (expenseId != -1L) {
            loadExpenseData();
        }

        btnSave.setOnClickListener(v -> saveExpense(v));
        btnCancel.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }

    private void loadExpenseData() {
        // Adatok betöltése szerkesztéshez (opcionális: ezt is megtehetnéd a repository-n keresztül)
        eventRepository.getExpensesForEvent(eventId, new RepositoryCallback<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> result) {
                for (Expense e : result) {
                    if (e.getId() != null && e.getId() == expenseId) {
                        requireActivity().runOnUiThread(() -> {
                            etDesc.setText(e.getDescription());
                            etAmount.setText(String.valueOf(e.getAmount()));
                            etPayer.setText(e.getPayer());
                            etParticipants.setText(e.getParticipants());
                        });
                        break;
                    }
                }
            }

            @Override
            public void onError(String message) {
                // Hiba kezelése
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

        // Új objektum létrehozása (vagy meglévő frissítése)
        Expense expense = new Expense(eventId, desc, amount, payer, participants);

        if (expenseId != -1L) {
            expense.setId(expenseId);
            // Itt hívhatnád az update-et a repository-ban, ha megírtuk
        }

        // KRITIKUS JAVÍTÁS: A Repository-t hívjuk meg!
        // Ez küldi el az adatot a Supabase-nek!
        eventRepository.saveExpense(expense, new RepositoryCallback<Expense>() {
            @Override
            public void onSuccess(Expense result) {
                // Csak akkor zárunk be és adunk hálát, ha a Supabase mentés sikeres volt
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Sikeres mentés!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).popBackStack();
                    });
                }
            }

            @Override
            public void onError(String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Hiba a felhőbe mentéskor: " + message, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}