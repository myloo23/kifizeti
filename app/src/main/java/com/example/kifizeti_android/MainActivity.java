package com.example.kifizeti_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kifizeti_android.data.UserSessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private UserSessionManager sessionManager;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        sessionManager = new UserSessionManager(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // JAVÍTÁS 1: Kivettük a 'R.id.nav_summary'-t a főképernyők listájából!
            // Eredmény: Az Elszámolás képernyőn meg fog jelenni a felső Vissza (<-) nyíl.
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_events, R.id.nav_add, R.id.nav_settings)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // JAVÍTÁS 2: Erős, egyedi menüvezérlés a beragadás ellen
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                // Kijelentkezés lekezelése
                if (itemId == R.id.nav_logout) {
                    sessionManager.logoutUser();
                    checkLoginStatus();
                    return false;
                }

                // Ha az Eseményekre kattintunk, agresszíven töröljük a köztes képernyőket
                if (itemId == R.id.nav_events) {
                    navController.popBackStack(R.id.nav_events, false);
                    return true;
                }

                // A többi gomb (Új, Beállítások, Elszámolás) normál navigációja
                return NavigationUI.onNavDestinationSelected(item, navController);
            });

            // Képernyőváltás figyelése: Elrejtjük a menüt login/register közben,
            // és frissítjük az alsó ikonok színét, ha visszalépünk.
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();

                if (destId == R.id.loginFragment || destId == R.id.registerFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                    if (getSupportActionBar() != null) getSupportActionBar().hide();
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    if (getSupportActionBar() != null) getSupportActionBar().show();
                }

                // Szinkronizáljuk a kék ikont azzal a képernyővel, ahol épp vagyunk
                if (destId == R.id.nav_events || destId == R.id.nav_add ||
                        destId == R.id.nav_summary || destId == R.id.nav_settings) {
                    bottomNavigationView.getMenu().findItem(destId).setChecked(true);
                }
            });
        }

        checkLoginStatus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void checkLoginStatus() {
        if (sessionManager != null && !sessionManager.isLoggedIn()) {
            if (navController != null) {
                navController.navigate(R.id.loginFragment);
            }
        }
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}