package com.example.kifizeti_android.ui.elszamolas;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.adapter.TartozasAdapter;
import com.example.kifizeti_android.data.Tartozas;
import com.example.kifizeti_android.service.ElszamolasService;

import java.util.List;
import java.util.Map;

public class ElszamolasFragment extends Fragment {

    private RecyclerView rvTartozasok;
    private TextView tvUresAllapot;
    private ElszamolasService elszamolasService;
    private int currentEventId = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elszamolas, container, false);

        rvTartozasok = view.findViewById(R.id.rv_tartozasok);
        tvUresAllapot = view.findViewById(R.id.tv_ures_allapot);
        rvTartozasok.setLayoutManager(new LinearLayoutManager(getContext()));

        elszamolasService = new ElszamolasService();

        if (getArguments() != null) {
            currentEventId = getArguments().getInt("eventId", 1);
        }

        frissitAdatokkal();

        return view;
    }

    private void frissitAdatokkal() {
        com.example.kifizeti_android.data.db.AppDatabase db =
                com.example.kifizeti_android.data.db.AppDatabase.getDatabase(getContext());
        com.example.kifizeti_android.data.dao.ExpenseDao expenseDao = db.expenseDao();

        List<com.example.kifizeti_android.data.entity.Expense> kiadasok = expenseDao.getExpensesForEvent(currentEventId);

        Map<String, Integer> egyenlegek = elszamolasService.kiadasokbolEgyenlegek(kiadasok);
        List<Tartozas> lista = elszamolasService.szamoldKiATartozasokat(egyenlegek);

        TartozasAdapter adapter = new TartozasAdapter(lista);
        rvTartozasok.setAdapter(adapter);

        if (lista.isEmpty()) {
            tvUresAllapot.setVisibility(View.VISIBLE);
            rvTartozasok.setVisibility(View.GONE);
        } else {
            tvUresAllapot.setVisibility(View.GONE);
            rvTartozasok.setVisibility(View.VISIBLE);
        }
    }
}