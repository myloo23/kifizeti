package com.example.kifizeti_android.ui.add;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;

public class AddEventFragment extends Fragment {

    private EditText etEventName, etEventDescription;
    private Button btnSaveEvent;
    private AppDatabase db;

    public AddEventFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEventName = view.findViewById(R.id.etEventName);
        etEventDescription = view.findViewById(R.id.etEventDescription);
        btnSaveEvent = view.findViewById(R.id.btnSaveEvent);

        db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "kifizeti_db")
                .allowMainThreadQueries()
                .build();

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String name = etEventName.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etEventName.setError("Az esemény neve kötelező");
            return;
        }

        Event event = new Event(name, description, System.currentTimeMillis());
        db.eventDao().insert(event);

        Toast.makeText(requireContext(), "Esemény mentve", Toast.LENGTH_SHORT).show();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.example.kifizeti_android.ui.events.EventsFragment())
                .commit();
    }
}