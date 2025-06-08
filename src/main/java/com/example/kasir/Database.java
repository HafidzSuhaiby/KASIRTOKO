package com.example.kasir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:vidajaya.db";

    // Method untuk connect ke SQLite
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Method untuk membuat tabel produk jika belum ada
    public static void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produk (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                harga REAL NOT NULL,
                stok INTEGER NOT NULL DEFAULT 0
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel produk siap digunakan.");
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel produk:");
            e.printStackTrace();
        }
    }
    public static void initFakturTable() {
        String sqlFaktur = """
            CREATE TABLE IF NOT EXISTS faktur (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nomor_faktur TEXT UNIQUE NOT NULL,
                tanggal TEXT NOT NULL,
                total_harga REAL NOT NULL,
                nama_pelanggan TEXT
            );
        """; 

        String sqlDetail = """
            CREATE TABLE IF NOT EXISTS detail_faktur (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                faktur_id INTEGER NOT NULL,
                produk_id INTEGER NOT NULL,
                jumlah INTEGER NOT NULL,
                harga_satuan REAL NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY (faktur_id) REFERENCES faktur(id),
                FOREIGN KEY (produk_id) REFERENCES produk(id)
            );
        """;

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sqlFaktur);
            stmt.execute(sqlDetail);
            System.out.println("Tabel faktur dan detail_faktur siap digunakan.");
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel faktur:");
            e.printStackTrace();
        }
    }
}
