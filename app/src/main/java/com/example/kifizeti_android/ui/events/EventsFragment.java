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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private EditText etSearch;
    private Spinner spinnerSort;
    private AppDatabase db;
    private ExecutorService executorService;
    
    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            loadEvents();
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    public EventsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ensureExecutorActive();
    }

    private synchronized void ensureExecutorActive() {
        if (executorService == null || executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        etSearch = view.findViewById(R.id.etSearch);
        spinnerSort = view.findViewById(R.id.spinnerSort);

        db = AppDatabase.getDatabase(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new EventAdapter(requireContext(), new ArrayList<>()));

        setupFilters();
    }

    private void setupFilters() {
        String[] sortOptions = {"Dátum", "ABC"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sortOptions
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);

        spinnerSort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                loadEvents();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        etSearch.removeTextChangedListener(searchWatcher);
        etSearch.addTextChangedListener(searchWatcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        if (etSearch == null || spinnerSort == null) return;
        
        ensureExecutorActive();
        
        String searchText = etSearch.getText().toString().trim();
        String selectedSort = spinnerSort.getSelectedItem() != null ? 
                spinnerSort.getSelectedItem().toString() : "Dátum";

        executorService.execute(() -> {
            try {
                List<Event> events;
                if (!searchText.isEmpty()) {
                    events = db.eventDao().searchEvents(searchText);
                } else {
                    if ("ABC".equals(selectedSort)) {
                        events = db.eventDao().getAllEventsByName();
                    } else {
                        events = db.eventDao().getAllEventsByDate();
                    }
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (events == null || events.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(new EventAdapter(requireContext(), events));
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (etSearch != null) {
            etSearch.removeTextChangedListener(searchWatcher);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
