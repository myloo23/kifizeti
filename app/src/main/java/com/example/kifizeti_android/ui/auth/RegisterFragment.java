package com.example.kifizeti_android.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.User;

public class RegisterFragment extends Fragment {

    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etUsername = view.findViewById(R.id.et_register_username);
        etEmail = view.findViewById(R.id.et_register_email);
        etPassword = view.findViewById(R.id.et_register_password);
        btnRegister = view.findViewById(R.id.btn_register);
        tvGoToLogin = view.findViewById(R.id.tv_go_to_login);

        db = AppDatabase.getDatabase(requireContext());

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Kérjük, töltsön ki minden mezőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.userDao().getUserByUsername(username) != null) {
                Toast.makeText(getContext(), "Ez a felhasználónév már foglalt!", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(username, password, email);
            db.userDao().registerUser(newUser);
            Toast.makeText(getContext(), "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
            
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        tvGoToLogin.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
