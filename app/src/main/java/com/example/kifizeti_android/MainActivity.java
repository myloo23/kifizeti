package com.example.kifizeti_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kifizeti_android.ui.add.AddEventFragment;
import com.example.kifizeti_android.ui.events.EventsFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // induláskor betöltjük az AddEventFragmentet
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new EventsFragment())
                .commit();
    }

}