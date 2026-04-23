package com.example.kifizeti_android;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.kifizeti_android.data.UserSessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private UserSessionManager sessionManager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        sessionManager = new UserSessionManager(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();


            NavigationUI.setupWithNavController(bottomNavigationView, navController);


            bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_logout) {
                    sessionManager.logoutUser();
                    checkLoginStatus();
                    return true;
                }

                return NavigationUI.onNavDestinationSelected(item, navController);
            });


            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.loginFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            });
        }

        checkLoginStatus();
    }

    public void checkLoginStatus() {
        if (sessionManager != null && !sessionManager.isLoggedIn()) {
            if (navController != null) {
                navController.navigate(R.id.loginFragment);
            }
        }
    }
}