package com.example.kasir.controller;

import com.example.kasir.Database;
import com.example.kasir.model.HistoryItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class HistoryController {

    @FXML
    private TableView<HistoryItem> historyTable;

    @FXML
    private TableColumn<HistoryItem, LocalDate> colTanggal;

    @FXML
    private TableColumn<HistoryItem, String> colProduk;

    @FXML
    private TableColumn<HistoryItem, Integer> colJumlah;

    private ObservableList<HistoryItem> historyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set cell value factory untuk kolom
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colProduk.setCellValueFactory(new PropertyValueFactory<>("produk"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));

        historyTable.setItems(historyList);

        loadHistory();
    }

    private void loadHistory() {
        String sql = """
            SELECT f.tanggal, p.nama AS produk, df.banyak
            FROM faktur f
            JOIN detail_faktur df ON f.no_faktur = df.no_faktur
            JOIN produk p ON df.kode_produk = p.kode
            ORDER BY f.tanggal DESC
            """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            historyList.clear();
            while (rs.next()) {
                LocalDate tanggal = rs.getDate("tanggal").toLocalDate();
                String produk = rs.getString("produk");
                int jumlah = rs.getInt("banyak");

                historyList.add(new HistoryItem(tanggal, produk, jumlah));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
