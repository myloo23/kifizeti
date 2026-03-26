package com.example.kifizeti_android.ui.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.adapter.EventAdapter;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;

import java.util.List;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
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

        db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "kifizeti_db")
                .allowMainThreadQueries()
                .build();

        loadEvents();
    }

    private void loadEvents() {
        List<Event> events = db.eventDao().getAllEvents();

        if (events.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(new EventAdapter(requireContext(), events));
        }
    }
}