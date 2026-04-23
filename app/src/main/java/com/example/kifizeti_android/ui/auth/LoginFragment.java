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
import androidx.navigation.Navigation;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.UserSessionManager;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private UserSessionManager sessionManager;
    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etUsername = view.findViewById(R.id.et_login_username);
        etPassword = view.findViewById(R.id.et_login_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvGoToRegister = view.findViewById(R.id.tv_go_to_register);

        sessionManager = new UserSessionManager(requireContext());
        db = AppDatabase.getDatabase(requireContext());

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Kérjük, töltsön ki minden mezőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                User user = db.userDao().login(username, password);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (user != null) {
                            sessionManager.createLoginSession(username);
                            Navigation.findNavController(view).navigate(R.id.nav_events);
                        } else {
                            Toast.makeText(getContext(), "Hibás felhasználónév vagy jelszó!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });

        tvGoToRegister.setOnClickListener(v -> {
             Navigation.findNavController(v).navigate(R.id.registerFragment);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
    }
}
