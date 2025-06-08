package com.example.kasir.model;

import java.time.LocalDate;

public class HistoryItem {
    private LocalDate tanggal;
    private String produk;
    private int jumlah;

    public HistoryItem(LocalDate tanggal, String produk, int jumlah) {
        this.tanggal = tanggal;
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public LocalDate getTanggal() { return tanggal; }
    public String getProduk() { return produk; }
    public int getJumlah() { return jumlah; }
}
