package com.example.kifizeti_android.data.network;

import com.example.kifizeti_android.data.entity.Event;
import com.example.kifizeti_android.data.entity.Expense; // Fontos az import!

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseService {

    // --- ESEMÉNYEK (EVENTS) ---

    @GET("events")
    Call<List<Event>> getEvents();

    @Headers({
            "Prefer: return=representation"
    })
    @POST("events")
    Call<List<Event>> createEvent(@Body Event event);

    @Headers({
            "Prefer: return=representation"
    })
    @PATCH("events")
    Call<List<Event>> updateEvent(@Query("id") String idFilter, @Body Event event);

    @DELETE("events")
    Call<Void> deleteEvent(@Query("id") String idFilter);


    // --- KIADÁSOK (EXPENSES) ---

    /**
     * Lekéri egy adott eseményhez tartozó összes kiadást.
     * @param eventIdFilter Szűrő formátuma: "eq.123"
     */
    @GET("expenses")
    Call<List<Expense>> getExpenses(@Query("event_id") String eventIdFilter);

    /**
     * Új kiadás létrehozása.
     * A fejléc miatt a Supabase visszaküldi a szerver által generált ID-t.
     */
    @Headers({
            "Prefer: return=representation"
    })
    @POST("expenses")
    Call<List<Expense>> createExpense(@Body Expense expense);

    /**
     * Kiadás törlése ID alapján.
     * @param idFilter Szűrő formátuma: "eq.456"
     */
    @DELETE("expenses")
    Call<Void> deleteExpense(@Query("id") String idFilter);


}