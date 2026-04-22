package com.example.kifizeti_android;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.kifizeti_android.data.UserSessionManager;
import com.example.kifizeti_android.ui.add.AddEventFragment;
import com.example.kifizeti_android.ui.auth.LoginFragment;
import com.example.kifizeti_android.ui.elszamolas.ElszamolasFragment;
import com.example.kifizeti_android.ui.events.EventsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        sessionManager = new UserSessionManager(this);

        checkLoginStatus();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_add) {
                selectedFragment = new AddEventFragment();
            } else if (item.getItemId() == R.id.nav_events) {
                selectedFragment = new EventsFragment();
            } else if (item.getItemId() == R.id.nav_summary) {
                selectedFragment = new ElszamolasFragment();
            } else if (item.getItemId() == R.id.nav_logout) {
                sessionManager.logoutUser();
                checkLoginStatus();
                return true;
            }

            return loadFragment(selectedFragment);
        });
    }

    public void checkLoginStatus() {
        if (!sessionManager.isLoggedIn()) {
            bottomNavigationView.setVisibility(View.GONE);
            loadFragment(new LoginFragment());
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            loadFragment(new EventsFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_events);
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
