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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Event> events;
    private final Context context;
    private final AppDatabase db;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
        this.db = Room.databaseBuilder(context, AppDatabase.class, "kifizeti_db")
                .allowMainThreadQueries()
                .build();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvDate;
        Button btnDelete;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvEventName);
            tvDescription = view.findViewById(R.id.tvEventDescription);
            tvDate = view.findViewById(R.id.tvEventDate);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.tvName.setText(event.getName());
        holder.tvDescription.setText(
                event.getDescription() == null || event.getDescription().trim().isEmpty()
                        ? "Nincs leírás"
                        : event.getDescription()
        );

        String formattedDate = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                .format(event.getCreatedAt());
        holder.tvDate.setText("Létrehozva: " + formattedDate);

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Esemény törlése")
                    .setMessage("Biztosan törölni szeretnéd ezt az eseményt?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            Event toDelete = events.get(adapterPosition);
                            db.eventDao().delete(toDelete);
                            events.remove(adapterPosition);
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Mégse", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}