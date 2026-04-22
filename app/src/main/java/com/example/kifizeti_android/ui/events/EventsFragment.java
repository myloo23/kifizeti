package com.example.kifizeti_android.ui.events;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.adapter.EventAdapter;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;

import java.util.List;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private EditText etSearch;
    private Spinner spinnerSort;
    private AppDatabase db;

    public EventsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerEvents);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        etSearch = view.findViewById(R.id.etSearch);
        spinnerSort = view.findViewById(R.id.spinnerSort);

        db = AppDatabase.getDatabase(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String[] sortOptions = {"Dátum", "ABC"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sortOptions
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);

        loadEvents();

        spinnerSort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                loadEvents();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadEvents();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadEvents() {
        String searchText = etSearch.getText().toString().trim();
        String selectedSort = spinnerSort.getSelectedItem().toString();

        List<Event> events;

        if (!searchText.isEmpty()) {
            events = db.eventDao().searchEvents(searchText);
        } else {
            if (selectedSort.equals("ABC")) {
                events = db.eventDao().getAllEventsByName();
            } else {
                events = db.eventDao().getAllEventsByDate();
            }
        }

        if (events.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new EventAdapter(requireContext(), events));
        }
    }
}