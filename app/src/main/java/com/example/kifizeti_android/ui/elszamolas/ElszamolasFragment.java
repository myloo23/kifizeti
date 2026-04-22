package com.example.kifizeti_android.ui.elszamolas;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifizeti_android.R;
import com.example.kifizeti_android.adapter.TartozasAdapter;
import com.example.kifizeti_android.data.Tartozas;
import com.example.kifizeti_android.service.ElszamolasService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElszamolasFragment extends Fragment {

    private RecyclerView rvTartozasok;
    private ElszamolasService elszamolasService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elszamolas, container, false);

        rvTartozasok = view.findViewById(R.id.rv_tartozasok);
        rvTartozasok.setLayoutManager(new LinearLayoutManager(getContext()));

        elszamolasService = new ElszamolasService();

        frissitAdatokkal();

        return view;
    }

    private void frissitAdatokkal() {
        com.example.kifizeti_android.data.db.AppDatabase db =
                com.example.kifizeti_android.data.db.AppDatabase.getDatabase(getContext());
        com.example.kifizeti_android.data.dao.ExpenseDao expenseDao = db.expenseDao();

        if (expenseDao.getExpensesForEvent(1).isEmpty()) {
            expenseDao.insert(new com.example.kifizeti_android.data.entity.Expense(
                    1, "Autópálya matrica és benzin", 9000.0, "Peti", "Peti, Gábor, Anna"));

            expenseDao.insert(new com.example.kifizeti_android.data.entity.Expense(
                    1, "Pizza vacsora", 6000.0, "Anna", "Peti, Anna"));

            expenseDao.insert(new com.example.kifizeti_android.data.entity.Expense(
                    1, "Buli belépők", 4500.0, "Gábor", "Peti, Gábor, Anna"));
        }
        List<com.example.kifizeti_android.data.entity.Expense> kiadasok = expenseDao.getExpensesForEvent(1);

        Map<String, Integer> egyenlegek = elszamolasService.kiadasokbolEgyenlegek(kiadasok);
        List<Tartozas> lista = elszamolasService.szamoldKiATartozasokat(egyenlegek);

        TartozasAdapter adapter = new TartozasAdapter(lista);
        rvTartozasok.setAdapter(adapter);
    }
}
