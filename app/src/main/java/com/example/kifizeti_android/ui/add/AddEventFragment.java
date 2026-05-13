package com.example.kifizeti_android.ui.add;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.entity.Event;
import com.example.kifizeti_android.data.repository.EventRepository;
import com.example.kifizeti_android.data.repository.RepositoryCallback;

public class AddEventFragment extends Fragment {

    private EditText etEventName, etEventDescription;
    private Spinner spinnerCategory;
    private Button btnSaveEvent;
    private EventRepository eventRepository;

    private boolean isEditMode = false;
    private long eventId = -1; // int-ről long-ra módosítva
    private long originalCreatedAt = 0;

    private final String[] categories = {"Egyéb", "Utazás", "Buli", "Étel", "Szórakozás"};

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
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnSaveEvent = view.findViewById(R.id.btnSaveEvent);

        eventRepository = new EventRepository(requireContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        if (getArguments() != null) {
            // A long típusú adatot getLong-gal kérjük le!
            eventId = getArguments().getLong("eventId", -1L);
            if (eventId != -1) {
                isEditMode = true;
                originalCreatedAt = getArguments().getLong("eventCreatedAt", System.currentTimeMillis());

                etEventName.setText(getArguments().getString("eventName", ""));
                etEventDescription.setText(getArguments().getString("eventDescription", ""));
                
                String category = getArguments().getString("eventCategory", "Egyéb");
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equals(category)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
                btnSaveEvent.setText(R.string.edit_save_button);
            }
        } else {
            btnSaveEvent.setText(R.string.create_button);
        }

        etEventName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSaveEvent.setOnClickListener(v -> saveEvent());
        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        if (etEventName == null) return;
        String name = etEventName.getText().toString().trim();
        boolean isValid = name.length() >= 3;
        btnSaveEvent.setEnabled(isValid);
        btnSaveEvent.setAlpha(isValid ? 1.0f : 0.5f);
    }

    private void saveEvent() {
        String name = etEventName.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            etEventName.setError(getString(R.string.required_field));
            return;
        }

        // Új Event objektum létrehozása
        Event event = new Event(
                name,
                description,
                isEditMode ? originalCreatedAt : System.currentTimeMillis(),
                category
        );
        
        // Ha szerkesztünk, beállítjuk az ID-t, különben NULL marad (így a Supabase generálja)
        if (isEditMode) {
            event.setId(eventId);
        }

        btnSaveEvent.setEnabled(false);
        btnSaveEvent.setText("Mentés...");

        eventRepository.saveEvent(event, new RepositoryCallback<Event>() {
            @Override
            public void onSuccess(Event result) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), 
                        isEditMode ? R.string.event_updated_toast : R.string.event_saved_toast, 
                        Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    btnSaveEvent.setEnabled(true);
                    btnSaveEvent.setText(isEditMode ? R.string.edit_save_button : R.string.create_button);
                    Toast.makeText(requireContext(), "Hiba (400): " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
