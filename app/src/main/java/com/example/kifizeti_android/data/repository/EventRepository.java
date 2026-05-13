package com.example.kifizeti_android.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;
import com.example.kifizeti_android.data.network.SupabaseClient;
import com.example.kifizeti_android.data.network.SupabaseService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepository {
    private final SupabaseService supabaseService;
    private final AppDatabase localDb;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public EventRepository(Context context) {
        this.supabaseService = SupabaseClient.getClient().create(SupabaseService.class);
        // Fontos: Az AppDatabase-nél a korábban megbeszélt "v2" vagy "v3" nevet használd!
        this.localDb = AppDatabase.getDatabase(context);
        this.executorService = Executors.newFixedThreadPool(4);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Esemény mentése a felhőbe, majd siker esetén a helyi adatbázisba.
     */
    public void saveEvent(Event event, RepositoryCallback<Event> callback) {
        supabaseService.createEvent(event).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // A Supabase visszaadja a mentett objektumot a generált ID-val
                    Event savedEvent = response.body().get(0);
                    executorService.execute(() -> {
                        localDb.eventDao().insert(savedEvent);
                        mainHandler.post(() -> callback.onSuccess(savedEvent));
                    });
                } else {
                    callback.onError("Supabase mentési hiba: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                callback.onError("Hálózati hiba mentéskor: " + t.getMessage());
            }
        });
    }

    /**
     * Letölti az összes eseményt. Először a helyit adja vissza,
     * majd frissít a felhőből érkező adatokkal.
     */
    public void getAllEvents(RepositoryCallback<List<Event>> callback) {
        executorService.execute(() -> {
            // 1. Azonnali válasz a helyi adatokkal
            List<Event> localEvents = localDb.eventDao().getAllEventsByDate();
            mainHandler.post(() -> callback.onSuccess(localEvents));

            // 2. Frissítés a Supabase-ről a háttérben
            supabaseService.getEvents().enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Event> cloudEvents = response.body();
                        executorService.execute(() -> {
                            for (Event e : cloudEvents) {
                                // ID alapú ellenőrzés név helyett!
                                if (e.getId() != null) {
                                    localDb.eventDao().insert(e);
                                    // Megjegyzés: A DAO insert-je OnConflictStrategy.REPLACE legyen!
                                }
                            }
                            List<Event> updatedLocal = localDb.eventDao().getAllEventsByDate();
                            mainHandler.post(() -> callback.onSuccess(updatedLocal));
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    // Csendes hiba: a felhasználó már látja a helyi adatokat
                }
            });
        });
    }

    /**
     * Esemény törlése az egyedi ID alapján mindkét helyről.
     */
    public void deleteEvent(Event event, RepositoryCallback<Void> callback) {
        if (event.getId() == null) {
            callback.onError("Hiba: Az esemény nem rendelkezik azonosítóval.");
            return;
        }

        // Supabase szűrő az ID alapján
        String filter = "eq." + event.getId();

        supabaseService.deleteEvent(filter).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                executorService.execute(() -> {
                    localDb.eventDao().delete(event);
                    mainHandler.post(() -> callback.onSuccess(null));
                });
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Törlés sikertelen (felhő hiba): " + t.getMessage());
            }
        });
    }

    public void searchEvents(String query, RepositoryCallback<List<Event>> callback) {
        executorService.execute(() -> {
            List<Event> results = localDb.eventDao().searchEvents(query);
            mainHandler.post(() -> callback.onSuccess(results));
        });
    }
}