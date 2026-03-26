package com.example.kifizeti_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;
    private Context context;
    private AppDatabase db;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;

        db = Room.databaseBuilder(context,
                        AppDatabase.class, "kifizeti_db")
                .allowMainThreadQueries()
                .build();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnDelete;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvEventName);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvName.setText(event.getName());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Törlés")
                    .setMessage("Biztos törlöd?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        db.eventDao().delete(event);
                        events.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}