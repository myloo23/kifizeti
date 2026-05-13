package com.example.kifizeti_android.data.network;

import com.example.kifizeti_android.data.entity.Event;

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
}
