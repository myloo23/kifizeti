package com.example.kifizeti_android.service;

import com.example.kifizeti_android.data.Tartozas;
import com.example.kifizeti_android.data.entity.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElszamolasService {

    public List<Tartozas> szamoldKiATartozasokat(Map<String, Integer> egyenlegek) {
        List<Tartozas> eredmeny = new ArrayList<>();

        // Két listát készítünk: kiknek jár pénz (hitelezők) és kik tartoznak (adósok)
        List<SzemelyEgyenleg> adosok = new ArrayList<>();
        List<SzemelyEgyenleg> hitelezok = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : egyenlegek.entrySet()) {
            if (entry.getValue() < 0) {
                adosok.add(new SzemelyEgyenleg(entry.getKey(), Math.abs(entry.getValue())));
            } else if (entry.getValue() > 0) {
                hitelezok.add(new SzemelyEgyenleg(entry.getKey(), entry.getValue()));
            }
        }

        int i = 0, j = 0;
        while (i < adosok.size() && j < hitelezok.size()) {
            SzemelyEgyenleg ados = adosok.get(i);
            SzemelyEgyenleg hitelezo = hitelezok.get(j);

            int fizetendo = Math.min(ados.osszeg, hitelezo.osszeg);

            if (fizetendo > 0) {
                eredmeny.add(new Tartozas(ados.nev, hitelezo.nev, fizetendo));
            }

            ados.osszeg -= fizetendo;
            hitelezo.osszeg -= fizetendo;

            if (ados.osszeg == 0) i++;
            if (hitelezo.osszeg == 0) j++;
        }
        return eredmeny;
    }

    private static class SzemelyEgyenleg {
        String nev;
        int osszeg;
        SzemelyEgyenleg(String nev, int osszeg) { this.nev = nev; this.osszeg = osszeg; }
    }
    public Map<String, Integer> kiadasokbolEgyenlegek(List<Expense> expenses) {
        Map<String, Integer> egyenlegek = new HashMap<>();

        for (Expense expense : expenses) {
            String payer = expense.getPayer();
            int amount = (int) Math.round(expense.getAmount());

            String[] participantArray = expense.getParticipants().split(",");
            int numOfParticipants = participantArray.length;

            if (numOfParticipants == 0) continue;

            int splitAmount = amount / numOfParticipants;

            egyenlegek.put(payer, egyenlegek.getOrDefault(payer, 0) + amount);

            for (String participant : participantArray) {
                String cleanName = participant.trim(); // Esetleges szóközök eltávolítása
                if (!cleanName.isEmpty()) {
                    egyenlegek.put(cleanName, egyenlegek.getOrDefault(cleanName, 0) - splitAmount);
                }
            }
        }

        return egyenlegek;
    }
}