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
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEventFragment extends Fragment {

    private EditText etEventName, etEventDescription;
    private Spinner spinnerCategory;
    private Button btnSaveEvent;
    private AppDatabase db;
    private ExecutorService executorService;

    private boolean isEditMode = false;
    private int eventId = -1;
    private long originalCreatedAt = 0;

    private final String[] categories = {"Egyéb", "Utazás", "Buli", "Étel", "Szórakozás"};

    public AddEventFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
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

        db = AppDatabase.getDatabase(requireContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        if (getArguments() != null) {
            isEditMode = true;
            eventId = getArguments().getInt("eventId", -1);
            originalCreatedAt = getArguments().getLong("eventCreatedAt", System.currentTimeMillis());

            String name = getArguments().getString("eventName", "");
            String description = getArguments().getString("eventDescription", "");
            String category = getArguments().getString("eventCategory", "Egyéb");

            etEventName.setText(name);
            etEventDescription.setText(description);
            
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(category)) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
            
            btnSaveEvent.setText(R.string.edit_save_button);
        } else {
            btnSaveEvent.setText(R.string.create_button);
        }

        etEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
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
        if (executorService == null || executorService.isShutdown()) return;

        String name = etEventName.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            etEventName.setError(getString(R.string.required_field));
            return;
        }

        executorService.execute(() -> {
            Event existingEvent = db.eventDao().getEventByExactName(name);
            
            if (getActivity() == null) return;

            getActivity().runOnUiThread(() -> {
                if (existingEvent != null && existingEvent.getId() != eventId) {
                    etEventName.setError(getString(R.string.event_exists_error));
                } else {
                    executorService.execute(() -> {
                        Event event = new Event(name, description, 
                            isEditMode ? originalCreatedAt : System.currentTimeMillis(), 
                            category);
                        
                        if (isEditMode) {
                            event.setId(eventId);
                            db.eventDao().update(event);
                        } else {
                            db.eventDao().insert(event);
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), 
                                    isEditMode ? R.string.event_updated_toast : R.string.event_saved_toast, 
                                    Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(requireView()).popBackStack();
                            });
                        }
                    });
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) executorService.shutdownNow();
    }
}
