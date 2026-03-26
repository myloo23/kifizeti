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

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.adapter.EventAdapter;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;

import java.util.List;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
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

        db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "kifizeti_db")
                .allowMainThreadQueries()
                .build();

        List<Event> events = db.eventDao().getAllEvents();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new EventAdapter(requireContext(), events));
    }
}