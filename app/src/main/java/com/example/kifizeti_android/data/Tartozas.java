package com.example.kifizeti_android.data;

public class Tartozas {
    private String ados;
    private String hitelezo;
    private int osszeg;

    public Tartozas(String ados, String hitelezo, int osszeg) {
        this.ados = ados;
        this.hitelezo = hitelezo;
        this.osszeg = osszeg;
    }

    public String getAdos() { return ados; }
    public String getHitelezo() { return hitelezo; }
    public int getOsszeg() { return osszeg; }
}