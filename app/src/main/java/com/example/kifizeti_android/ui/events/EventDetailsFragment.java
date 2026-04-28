package com.example.kifizeti_android.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.adapter.ExpenseAdapter;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventDetailsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_EVENT_NAME = "event_name";
    private static final String ARG_EVENT_DESC = "event_desc";

    private int eventId;
    private String eventName;
    private String eventDesc;

    private AppDatabase db;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
            eventName = getArguments().getString(ARG_EVENT_NAME);
            eventDesc = getArguments().getString(ARG_EVENT_DESC);
        }
        db = AppDatabase.getDatabase(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        TextView tvName = view.findViewById(R.id.tvDetailEventName);
        TextView tvDesc = view.findViewById(R.id.tvDetailEventDesc);
        recyclerView = view.findViewById(R.id.recyclerExpenses);
        Button btnAddExpense = view.findViewById(R.id.btnAddExpense);
        Button btnBack = view.findViewById(R.id.btnBack);

        tvName.setText(eventName);
        tvDesc.setText(eventDesc != null && !eventDesc.isEmpty() ? eventDesc : "Nincs leírás");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddExpense.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("event_id", eventId);
            Navigation.findNavController(v).navigate(R.id.nav_add_expense, bundle);
        });

        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        executorService.execute(() -> {
            List<Expense> expenses = db.expenseDao().getExpensesForEvent(eventId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter = new ExpenseAdapter(getContext(), expenses, eventId);
                    recyclerView.setAdapter(adapter);
                });
            }
        });
    }
}
