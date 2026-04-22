package com.example.kifizeti_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.data.Tartozas;

import java.util.List;

public class TartozasAdapter extends RecyclerView.Adapter<TartozasAdapter.TartozasViewHolder> {

    private List<Tartozas> tartozasList;

    public TartozasAdapter(List<Tartozas> tartozasList) {
        this.tartozasList = tartozasList;
    }

    @NonNull
    @Override
    public TartozasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tartozas, parent, false);
        return new TartozasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TartozasViewHolder holder, int position) {
        Tartozas tartozas = tartozasList.get(position);

        holder.tvKiTartozik.setText(tartozas.getAdos());
        holder.tvKinekTartozik.setText(tartozas.getHitelezo());
        holder.tvOsszeg.setText(tartozas.getOsszeg() + " Ft");
    }

    @Override
    public int getItemCount() {
        return tartozasList.size();
    }

    static class TartozasViewHolder extends RecyclerView.ViewHolder {
        TextView tvKiTartozik, tvKinekTartozik, tvOsszeg;

        public TartozasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKiTartozik = itemView.findViewById(R.id.tv_ki_tartozik);
            tvKinekTartozik = itemView.findViewById(R.id.tv_kinek_tartozik);
            tvOsszeg = itemView.findViewById(R.id.tv_osszeg);
        }
    }
}