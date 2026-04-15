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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.adapter.ExpenseAdapter;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Expense;

import java.util.List;

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

    public static EventDetailsFragment newInstance(int eventId, String name, String desc) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        args.putString(ARG_EVENT_NAME, name);
        args.putString(ARG_EVENT_DESC, desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
            eventName = getArguments().getString(ARG_EVENT_NAME);
            eventDesc = getArguments().getString(ARG_EVENT_DESC);
        }
        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "kifizeti_db")
                .allowMainThreadQueries()
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        TextView tvName = view.findViewById(R.id.tvDetailEventName);
        TextView tvDesc = view.findViewById(R.id.tvDetailEventDesc);
        recyclerView = view.findViewById(R.id.recyclerExpenses);
        Button btnAddExpense = view.findViewById(R.id.btnAddExpense);

        tvName.setText(eventName);
        tvDesc.setText(eventDesc);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadExpenses();

        btnAddExpense.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AddExpenseFragment.newInstance(eventId, -1))
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadExpenses() {
        List<Expense> expenses = db.expenseDao().getExpensesForEvent(eventId);
        adapter = new ExpenseAdapter(getContext(), expenses, eventId);
        recyclerView.setAdapter(adapter);
    }
}
