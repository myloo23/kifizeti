package com.example.kifizeti_android.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;
import com.example.kifizeti_android.data.entity.Expense;
import com.example.kifizeti_android.data.network.SupabaseClient;
import com.example.kifizeti_android.data.network.SupabaseService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A Repository osztály, amely összeköti a Supabase felhőt a helyi Room adatbázissal.
 */
public class EventRepository {
    private final SupabaseService supabaseService;
    private final AppDatabase localDb;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public EventRepository(Context context) {
        this.supabaseService = SupabaseClient.getClient().create(SupabaseService.class);
        this.localDb = AppDatabase.getDatabase(context);
        this.executorService = Executors.newFixedThreadPool(4);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // --- KIADÁSOK KEZELÉSE (EXPENSES) ---

    /**
     * Új kiadás mentése: Először Supabase-be küldjük, majd a kapott ID-val mentjük a Room-ba.
     */
    public void saveExpense(Expense expense, RepositoryCallback<Expense> callback) {
        supabaseService.createExpense(expense).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // A Supabase visszaküldte a mentett objektumot a generált ID-val
                    Expense savedExpense = response.body().get(0);

                    executorService.execute(() -> {
                        // Mentés a helyi adatbázisba
                        localDb.expenseDao().insert(savedExpense);
                        mainHandler.post(() -> callback.onSuccess(savedExpense));
                    });
                    Log.d("Supabase", "Kiadás sikeresen mentve a felhőbe.");
                } else {
                    Log.e("Supabase", "Hiba a mentéskor: " + response.code());
                    callback.onError("Supabase hiba: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Log.e("Supabase", "Hálózati hiba: " + t.getMessage());
                callback.onError("Hálózati hiba: " + t.getMessage());
            }
        });
    }

    /**
     * Kiadások letöltése egy adott eseményhez. Megpróbálja a felhőből, hiba esetén a helyiből.
     */
    public void getExpensesForEvent(long eventId, RepositoryCallback<List<Expense>> callback) {
        String filter = "eq." + eventId;
        supabaseService.getExpenses(filter).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> cloudExpenses = response.body();
                    executorService.execute(() -> {
                        for (Expense e : cloudExpenses) {
                            localDb.expenseDao().insert(e);
                        }
                        mainHandler.post(() -> callback.onSuccess(cloudExpenses));
                    });
                } else {
                    // Ha a hálózat sikertelen (pl. 500-as hiba), a helyi DAO-ból töltünk be
                    loadExpensesFromLocal(eventId, callback);
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                // Hálózati hiba (nincs internet) esetén is a helyi adatbázishoz fordulunk
                loadExpensesFromLocal(eventId, callback);
            }
        });
    }

    /**
     * Segédmetódus a helyi betöltéshez (hogy ne duplikáljuk a kódot).
     */
    private void loadExpensesFromLocal(long eventId, RepositoryCallback<List<Expense>> callback) {
        executorService.execute(() -> {
            List<Expense> localExpenses = localDb.expenseDao().getExpensesByEventId(eventId);
            mainHandler.post(() -> callback.onSuccess(localExpenses));
        });
    }

    // --- ESEMÉNYEK KEZELÉSE (EVENTS) ---

    public void saveEvent(Event event, RepositoryCallback<Event> callback) {
        supabaseService.createEvent(event).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
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
                callback.onError("Hálózati hiba: " + t.getMessage());
            }
        });
    }

    public void getAllEvents(RepositoryCallback<List<Event>> callback) {
        executorService.execute(() -> {
            // 1. Azonnali válasz a helyi adatokkal a gyorsabb UI érdekében
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
                                if (e.getId() != null) {
                                    localDb.eventDao().insert(e);
                                }
                            }
                            List<Event> updatedLocal = localDb.eventDao().getAllEventsByDate();
                            mainHandler.post(() -> callback.onSuccess(updatedLocal));
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    Log.e("Repository", "Nem sikerült frissíteni a felhőből: " + t.getMessage());
                }
            });
        });
    }

    public void deleteEvent(Event event, RepositoryCallback<Void> callback) {
        if (event.getId() == null) return;
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
                callback.onError("Törlési hiba: " + t.getMessage());
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