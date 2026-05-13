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
import com.example.kifizeti_android.data.db.AppDatabase;
import com.example.kifizeti_android.data.dao.ExpenseDao;
import com.example.kifizeti_android.data.entity.Expense;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElszamolasFragment extends Fragment {

    private RecyclerView rvTartozasok;
    private TextView tvUresAllapot;
    private ElszamolasService elszamolasService;

    private long currentEventId = -1L;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elszamolas, container, false);

        rvTartozasok = view.findViewById(R.id.rv_tartozasok);
        tvUresAllapot = view.findViewById(R.id.tv_ures_allapot);
        rvTartozasok.setLayoutManager(new LinearLayoutManager(getContext()));

        elszamolasService = new ElszamolasService();

        // 1. Lépés: Adat fogadása
        if (getArguments() != null) {
            if (getArguments().containsKey("event_id")) {
                currentEventId = getArguments().getLong("event_id", -1L);
            } else if (getArguments().containsKey("eventId")) {
                currentEventId = getArguments().getLong("eventId", -1L);
            }
        }

        frissitAdatokkal();

        return view;
    }

    private void frissitAdatokkal() {
        // Ha nem kaptunk ID-t (pl. az alsó menüt használta a felhasználó a gomb helyett)
        if (currentEventId == -1L) {
            mutasdAzUresAllapotot("Kérlek, az Esemény részleteinél lévő lila gombot használd az elszámoláshoz!");
            return;
        }

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getContext());
            ExpenseDao expenseDao = db.expenseDao();

            // 2. Lépés: Lekérjük a Nyers kiadásokat

            List<Expense> kiadasok = expenseDao.getExpensesByEventId(currentEventId);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {

                    // Ha az adatbázis teljesen üres
                    if (kiadasok.isEmpty()) {
                        mutasdAzUresAllapotot("Még nincsenek kiadások rögzítve ebben az eseményben.");
                        return;
                    }

                    // 3. Lépés: Ha vannak adatok, számoljunk velük!
                    Map<String, Integer> egyenlegek = elszamolasService.kiadasokbolEgyenlegek(kiadasok);
                    List<Tartozas> lista = elszamolasService.szamoldKiATartozasokat(egyenlegek);

                    // Ha a számolás eredménye üres (pl. elírás a nevekben, vagy senki sem tartozik)
                    if (lista.isEmpty()) {
                        mutasdAzUresAllapotot("Kiadások megtalálva, de a számítás alapján senki sem tartozik senkinek! (Vagy elírás van a nevekben)");
                    } else {
                        // 4. Lépés: Minden tökéletes, mutassuk a listát!
                        tvUresAllapot.setVisibility(View.GONE);
                        rvTartozasok.setVisibility(View.VISIBLE);
                        TartozasAdapter adapter = new TartozasAdapter(lista);
                        rvTartozasok.setAdapter(adapter);
                    }
                });
            }
        });
    }


    private void mutasdAzUresAllapotot(String uzenet) {
        if (tvUresAllapot != null) {
            tvUresAllapot.setText(uzenet);
            tvUresAllapot.setVisibility(View.VISIBLE);
        }
        if (rvTartozasok != null) {
            rvTartozasok.setVisibility(View.GONE);
        }
    }
}