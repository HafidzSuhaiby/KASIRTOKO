package com.example.kasir.model;

public class ItemFaktur {
    private String kode;
    private String item;
    private double harga;
    private int banyak;
    private double diskon;

    public ItemFaktur(String kode, String item, double harga, int banyak, double diskon) {
        this.kode = kode;
        this.item = item;
        this.harga = harga;
        this.banyak = banyak;
        this.diskon = diskon;
    }

    public String getKode() {
        return kode;
    }

    public String getItem() {
        return item;
    }

    public double getHarga() {
        return harga;
    }

    public int getBanyak() {
        return banyak;
    }

    public void setBanyak(int banyak) {
        this.banyak = banyak;
    }

    public double getDiskon() {
        return diskon;
    }

    public double getTotal() {
        return banyak * harga * (1 - diskon / 100);
    }
}
