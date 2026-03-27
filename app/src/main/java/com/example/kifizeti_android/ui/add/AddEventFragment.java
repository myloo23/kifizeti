package com.example.kifizeti_android.ui.add;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.kifizeti_android.ui.events.EventsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddEventFragment extends Fragment {

    private EditText etEventName, etEventDescription;
    private Button btnSaveEvent;
    private AppDatabase db;

    private boolean isEditMode = false;
    private int eventId = -1;

    public AddEventFragment() {
    }

    public static AddEventFragment newInstance(int id, String name, String description) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putInt("eventId", id);
        args.putString("eventName", name);
        args.putString("eventDescription", description);
        fragment.setArguments(args);
        return fragment;
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

        if (getArguments() != null) {
            isEditMode = true;
            eventId = getArguments().getInt("eventId", -1);

            String name = getArguments().getString("eventName", "");
            String description = getArguments().getString("eventDescription", "");

            etEventName.setText(name);
            etEventDescription.setText(description);
            btnSaveEvent.setText("Módosítás mentése");
        } else {
            btnSaveEvent.setText("Mentés");
        }

        etEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSaveButtonState();
            }
        });

        btnSaveEvent.setOnClickListener(v -> saveEvent());

        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        String name = etEventName.getText().toString().trim();
        boolean isValid = name.length() >= 3;

        btnSaveEvent.setEnabled(isValid);
        btnSaveEvent.setAlpha(isValid ? 1.0f : 0.5f);
    }

    private void saveEvent() {
        String name = etEventName.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etEventName.setError("Kötelező mező");
            etEventName.requestFocus();
            return;
        }

        if (name.length() < 3) {
            etEventName.setError("Legalább 3 karakter");
            etEventName.requestFocus();
            return;
        }

        if (!isEditMode) {
            Event existingEvent = db.eventDao().getEventByExactName(name);
            if (existingEvent != null) {
                etEventName.setError("Ilyen nevű esemény már létezik");
                etEventName.requestFocus();
                return;
            }
        }

        if (isEditMode) {
            Event event = new Event(name, description, System.currentTimeMillis());
            event.setId(eventId);
            db.eventDao().update(event);
            Toast.makeText(requireContext(), "Esemény módosítva", Toast.LENGTH_SHORT).show();
        } else {
            Event event = new Event(name, description, System.currentTimeMillis());
            db.eventDao().insert(event);
            Toast.makeText(requireContext(), "Esemény mentve", Toast.LENGTH_SHORT).show();
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new EventsFragment())
                .commit();

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_events);
    }
}