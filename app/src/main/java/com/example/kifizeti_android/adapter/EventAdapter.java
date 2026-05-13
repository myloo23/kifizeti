package com.example.kifizeti_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.entity.Event;
import com.example.kifizeti_android.data.repository.EventRepository;
import com.example.kifizeti_android.data.repository.RepositoryCallback;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Event> events;
    private final Context context;
    private final EventRepository eventRepository;

    public EventAdapter(Context context, List<Event> events, EventRepository eventRepository) {
        this.context = context;
        this.events = events;
        this.eventRepository = eventRepository;
    }

    public void updateEvents(List<Event> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvDate;
        Button btnDelete, btnEdit;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvEventName);
            tvDescription = view.findViewById(R.id.tvEventDescription);
            tvDate = view.findViewById(R.id.tvEventDate);
            btnDelete = view.findViewById(R.id.btnDelete);
            btnEdit = view.findViewById(R.id.btnEdit);
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
                        ? context.getString(R.string.no_description)
                        : event.getDescription()
        );

        String formattedDate = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                .format(event.getCreatedAt());
        holder.tvDate.setText(context.getString(R.string.created_at_format, formattedDate));

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("event_id", event.getId());
            bundle.putString("event_name", event.getName());
            bundle.putString("event_desc", event.getDescription());
            bundle.putString("event_category", event.getCategory());
            Navigation.findNavController(v).navigate(R.id.nav_event_details, bundle);
        });

        holder.btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("eventId", event.getId());
            bundle.putString("eventName", event.getName());
            bundle.putString("eventDescription", event.getDescription());
            bundle.putLong("eventCreatedAt", event.getCreatedAt());
            bundle.putString("eventCategory", event.getCategory());

            Navigation.findNavController(v).navigate(R.id.nav_add, bundle);
        });

        holder.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle(R.string.delete_event_title)
                .setMessage(R.string.delete_event_message)
                .setPositiveButton(R.string.yes_text, (dialog, which) -> {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        eventRepository.deleteEvent(event, new RepositoryCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                events.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                Toast.makeText(context, "Esemény törölve", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(context, "Hiba a törlésnél: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.no_text, null)
                .show());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}