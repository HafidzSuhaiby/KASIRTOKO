package com.example.kasir.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.kasir.Database;
import com.example.kasir.model.Produk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class ProdukController {

    @FXML
    private TextField namaProdukField;

    @FXML
    private TextField hargaProdukField;

    @FXML
    private Button tambahButton;

    @FXML
    private ListView<Produk> listViewProduk;

    private ObservableList<Produk> produkList = FXCollections.observableArrayList();

    public void initialize() {
        listViewProduk.setItems(produkList);

        // Atur tampilan listview agar menampilkan nama produk saja
        listViewProduk.setCellFactory(param -> new ListCell<Produk>() {
            @Override
            protected void updateItem(Produk item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNama() + " - Rp " + String.format("%,.0f", item.getHarga()));
                }
            }
        });

        loadDataFromDatabase();

        // Tambah validasi agar tombol tambah hanya aktif jika input valid
        tambahButton.setDisable(true);
        namaProdukField.textProperty().addListener((obs, oldText, newText) -> validateInput());
        hargaProdukField.textProperty().addListener((obs, oldText, newText) -> validateInput());
    }

    private void validateInput() {
        String nama = namaProdukField.getText().trim();
        String harga = hargaProdukField.getText().trim();
        boolean validHarga = false;

        try {
            double h = Double.parseDouble(harga);
            validHarga = h >= 0;
        } catch (NumberFormatException e) {
            validHarga = false;
        }

        tambahButton.setDisable(nama.isEmpty() || harga.isEmpty() || !validHarga);
    }

    private void loadDataFromDatabase() {
        String sql = "SELECT id, nama, harga, stok FROM produk";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            produkList.clear();

            while (rs.next()) {
                Produk produk = new Produk(
                    rs.getString("id"),
                    rs.getString("nama"),
                    rs.getDouble("harga"),
                    rs.getInt("stok")
                );
                produkList.add(produk);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading data produk: " + e.getMessage());
        }
    }

    @FXML
    private void tambahProduk() {
        String nama = namaProdukField.getText().trim();
        String hargaText = hargaProdukField.getText().trim();

        if (nama.isEmpty() || hargaText.isEmpty()) {
            showAlert("Nama dan harga harus diisi");
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(hargaText);
            if (harga < 0) {
                showAlert("Harga tidak boleh negatif");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Harga harus berupa angka");
            return;
        }

        String sql = "INSERT INTO produk (nama, harga, stok) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nama);
            ps.setDouble(2, harga);
            ps.setInt(3, 0);  // default stok 0 saat tambah produk baru
            ps.executeUpdate();

            // Refresh data
            loadDataFromDatabase();

            // Clear input
            namaProdukField.clear();
            hargaProdukField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menambahkan produk: " + e.getMessage());
        }
    }

    private void showAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
