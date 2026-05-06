package com.example.kifizeti_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.entity.Event;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Event> events;
    private final Context context;
    private final AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
        this.db = AppDatabase.getDatabase(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvDate, tvCategory;
        Button btnDelete, btnEdit;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvEventName);
            tvDescription = view.findViewById(R.id.tvEventDescription);
            tvDate = view.findViewById(R.id.tvEventDate);
            tvCategory = view.findViewById(R.id.tvEventCategory);
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
        
        if (holder.tvCategory != null) {
            holder.tvCategory.setText(event.getCategory() != null ? event.getCategory() : context.getString(R.string.category_placeholder));
        }

        String formattedDate = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                .format(event.getCreatedAt());
        holder.tvDate.setText(context.getString(R.string.created_at_format, formattedDate));

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("event_id", event.getId());
            bundle.putString("event_name", event.getName());
            bundle.putString("event_desc", event.getDescription());
            bundle.putString("event_category", event.getCategory());
            Navigation.findNavController(v).navigate(R.id.nav_event_details, bundle);
        });

        holder.btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("eventId", event.getId());
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
                        Event toDelete = events.get(adapterPosition);
                        executorService.execute(() -> {
                            db.eventDao().delete(toDelete);
                            if (context instanceof AppCompatActivity) {
                                ((AppCompatActivity) context).runOnUiThread(() -> {
                                    events.remove(adapterPosition);
                                    notifyItemRemoved(adapterPosition);
                                });
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