package com.example.kasir.controller;

import com.example.kasir.Database;
import com.example.kasir.model.ItemFaktur;
import com.example.kasir.model.Produk;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.geometry.Insets;


import java.sql.*;
import java.time.LocalDate;

public class HomeController {

    @FXML
    private ComboBox<Produk> comboProduk;

    @FXML
    private ComboBox<String> comboPelanggan;

    @FXML
    private TableView<ItemFaktur> tableFaktur;

    @FXML
    private TableColumn<ItemFaktur, String> colKode;
    @FXML
    private TableColumn<ItemFaktur, String> colItem;
    @FXML
    private TableColumn<ItemFaktur, Double> colHarga;
    @FXML
    private TableColumn<ItemFaktur, Integer> colBanyak;
    @FXML
    private TableColumn<ItemFaktur, Double> colDiskon;
    @FXML
    private TableColumn<ItemFaktur, Double> colTotal;

    @FXML
    private Label labelTotal;
    @FXML
    private Label labelJumlah;
    @FXML
    private Label labelSubTotal;
    @FXML
    private Label labelPotongan;
    @FXML
    private Label labelPPN;
    @FXML
    private Label labelTotalBayar;
    @FXML
    private Label labelKembali;
    @FXML
    private Label labelTotalHarga;

    @FXML
    private TextField fieldDibayar;
    @FXML
    private TextField fieldNoFaktur;

    @FXML
    private DatePicker dateFaktur;

    @FXML
    private ImageView imageProduk;

    @FXML
    private VBox strukArea;

    @FXML
    private Button btnFakturBaru, btnSimpan, btnPrint, btnHapusFaktur, btnHapusBaris, btnExportXLS, btnRefreshProduk;


    @FXML
    private void onTambahPelanggan() {
        System.out.println("Tambah pelanggan dipanggil");
    }

    @FXML
    private void onExportXLS(ActionEvent event) {
        // Implementasi ekspor ke Excel
        System.out.println("Export ke XLS diklik!");
    }

    @FXML
    private void onHapusBaris(ActionEvent event) {
        // kode untuk hapus baris di sini
        System.out.println("Tombol hapus baris ditekan");
    }

    @FXML
    private void onRefreshProduk(ActionEvent event) {
        System.out.println("Refresh produk diklik!");
        // Tambahkan logika refresh di sini
    }


    private ObservableList<Produk> produkList = FXCollections.observableArrayList();
    private ObservableList<ItemFaktur> fakturList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Database.initDatabase();
        Database.initFakturTable();
        dateFaktur.setValue(LocalDate.now());

        // Setup table columns
        colKode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKode()));
        colItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem()));
        colHarga.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getHarga()).asObject());
        colBanyak.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBanyak()).asObject());
        colDiskon.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getDiskon()).asObject());
        colTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());

        tableFaktur.setItems(fakturList);

        loadProduk();

        comboPelanggan.setItems(FXCollections.observableArrayList("Pelanggan A", "Pelanggan B", "Pelanggan C"));

        fakturList.addListener((javafx.collections.ListChangeListener.Change<? extends ItemFaktur> c) -> updateSummary());

        fieldDibayar.textProperty().addListener((obs, oldVal, newVal) -> hitungKembali());

        // Set nomor faktur otomatis saat inisialisasi
        fieldNoFaktur.setText(generateNoFakturOtomatis());

        btnFakturBaru.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                System.out.println("Scene is: " + newScene);

                newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F5),
                    () -> {
                        System.out.println("F5 ditekan");
                        btnFakturBaru.fire();
                    }
                );

                newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F10),
                    () -> {
                        System.out.println("F10 ditekan");
                        btnSimpan.fire();
                    }
                );

                newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F7),
                    () -> {
                        System.out.println("F7 ditekan");
                        btnPrint.fire();
                    }
                );

                newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F9),
                    () -> {
                        System.out.println("F9 ditekan");
                        btnHapusFaktur.fire();
                    }
                );

                newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F12),
                    () -> {
                        System.out.println("F12 ditekan");
                        btnHapusBaris.fire();
                    }
                );

                newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F6),
                    () -> {
                        System.out.println("F6 ditekan");
                        btnExportXLS.fire();
                    }
                );

                newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F1),
                    () -> {
                        System.out.println("F1 ditekan");
                        btnRefreshProduk.fire();
                    }
                );
            }
        });
    }

    private void loadProduk() {
        produkList.clear();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nama, harga, stok FROM produk")) {
            while (rs.next()) {
                Produk p = new Produk(
                        rs.getString("id"),
                        rs.getString("nama"),
                        rs.getDouble("harga"),
                        rs.getInt("stok"));
                produkList.add(p);
            }
            comboProduk.setItems(produkList);

            if (produkList.isEmpty()) {
                showAlert("Daftar produk kosong. Silakan tambahkan produk terlebih dahulu.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal memuat produk: " + e.getMessage());
        }
    }
    
    @FXML
    private void onPrint(ActionEvent event) {
        System.out.println("Tombol Print ditekan");

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            Node strukNode = createStrukNode(); // Buat node yang mewakili struk

            boolean success = job.printPage(strukNode);
            if (success) {
                job.endJob();
                System.out.println("Struk berhasil dicetak.");
            } else {
                System.out.println("Gagal mencetak struk.");
            }
        } else {
            System.out.println("Printer tidak tersedia.");
        }
    }


    private Node createStrukNode() {
        VBox struk = new VBox(5);
        struk.setPadding(new Insets(10));
        struk.setStyle("-fx-font-family: 'monospaced';");

        struk.getChildren().add(new Label("=== TOKO KASIRKU ==="));
        struk.getChildren().add(new Label("Tanggal: " + LocalDate.now()));
        struk.getChildren().add(new Label("---------------------------"));

        for (ItemFaktur item : tableFaktur.getItems()) {
            String nama = item.getItem();
            int banyak = item.getBanyak();
            double total = item.getTotal();
            struk.getChildren().add(new Label(nama + " x" + banyak + " .... " + total));
        }

        struk.getChildren().add(new Label("---------------------------"));
        struk.getChildren().add(new Label("Total Bayar: Rp " + labelTotalBayar.getText()));
        struk.getChildren().add(new Label("Dibayar: Rp " + fieldDibayar.getText()));
        struk.getChildren().add(new Label("Kembali: Rp " + labelKembali.getText()));
        struk.getChildren().add(new Label("==========================="));
        struk.getChildren().add(new Label("Terima kasih!"));

        return struk;
    }

    @FXML
    private void onTambahProduk() {
        Produk selected = comboProduk.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Pilih produk terlebih dahulu.");
            return;
        }

        for (ItemFaktur item : fakturList) {
            if (item.getKode().equals(selected.getKode())) {
                item.setBanyak(item.getBanyak() + 1);
                tableFaktur.refresh();
                updateSummary();
                return;
            }
        }

        ItemFaktur baru = new ItemFaktur(selected.getKode(), selected.getNama(), selected.getHarga(), 1, 0);
        fakturList.add(baru);
        updateSummary();
    }

    private void updateSummary() {
        double subTotal = 0;
        int totalJumlah = 0;
        for (ItemFaktur item : fakturList) {
            subTotal += item.getTotal();
            totalJumlah += item.getBanyak();
        }

        double potongan = subTotal * 0;
        double ppn = (subTotal - potongan) * 0;
        double totalBayar = subTotal - potongan + ppn;

        labelSubTotal.setText(String.format("%,.0f", subTotal));
        labelJumlah.setText(String.valueOf(totalJumlah));
        labelPotongan.setText(String.format("%,.0f", potongan));
        labelPPN.setText(String.format("%,.0f", ppn));
        labelTotalBayar.setText(String.format("%,.0f", totalBayar));
        labelTotal.setText(String.format("%,.0f", totalBayar));

        hitungKembali();
    }

    private void hitungKembali() {
        String dibayarText = fieldDibayar.getText().replaceAll("[^\\d]", "");
        double dibayar = 0;
        if (!dibayarText.isEmpty()) {
            try {
                dibayar = Double.parseDouble(dibayarText);
            } catch (NumberFormatException e) {
                showAlert("Input dibayar tidak valid.");
                return;
            }
        }

        double totalBayar = parseCurrency(labelTotalBayar.getText());
        double kembali = dibayar - totalBayar;
        if (kembali < 0) kembali = 0;

        labelKembali.setText(String.format("%,.0f", kembali));
    }

    private double parseCurrency(String text) {
        try {
            return Double.parseDouble(text.replaceAll("[^\\d]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    @FXML
    private void onFakturBaru() {
        fakturList.clear();
        fieldNoFaktur.setText(generateNoFakturOtomatis());
        fieldDibayar.clear();
        dateFaktur.setValue(LocalDate.now());
        labelKembali.setText("0");
        updateSummary();
    }

    @FXML
    private void onSimpan() {
        if (fakturList.isEmpty()) {
            showAlert("Daftar faktur kosong!");
            return;
        }

        String noFaktur = fieldNoFaktur.getText().trim();
        LocalDate tanggal = dateFaktur.getValue();

        if (noFaktur.isEmpty() || tanggal == null) {
            showAlert("Nomor faktur dan tanggal harus diisi.");
            return;
        }

        if (isFakturExist(noFaktur)) {
            showAlert("Nomor faktur sudah ada. Gunakan nomor lain.");
            return;
        }

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);

            String sqlFaktur = "INSERT INTO faktur (nomor_faktur, tanggal, total_harga, nama_pelanggan) VALUES (?, ?, ?, ?)";
            int fakturId = -1;
            try (PreparedStatement psFaktur = conn.prepareStatement(sqlFaktur, Statement.RETURN_GENERATED_KEYS)) {
                double totalBayar = fakturList.stream().mapToDouble(ItemFaktur::getTotal).sum();
                psFaktur.setString(1, noFaktur);
                psFaktur.setString(2, tanggal.toString());
                psFaktur.setDouble(3, totalBayar);
                psFaktur.setString(4, comboPelanggan.getValue() != null ? comboPelanggan.getValue() : null);
                psFaktur.executeUpdate();

                ResultSet rs = psFaktur.getGeneratedKeys();
                if (rs.next()) {
                    fakturId = rs.getInt(1);
                }
            }

            if (fakturId == -1) {
                conn.rollback();
                showAlert("Gagal mendapatkan ID faktur.");
                return;
            }

            String sqlDetail = "INSERT INTO detail_faktur (faktur_id, produk_id, jumlah, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for (ItemFaktur item : fakturList) {
                    int produkId = getProdukIdByKode(conn, item.getKode());
                    if (produkId == -1) {
                        conn.rollback();
                        showAlert("Produk dengan kode " + item.getKode() + " tidak ditemukan.");
                        return;
                    }
                    psDetail.setInt(1, fakturId);
                    psDetail.setInt(2, produkId);
                    psDetail.setInt(3, item.getBanyak());
                    psDetail.setDouble(4, item.getHarga());
                    psDetail.setDouble(5, item.getTotal());
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            conn.commit();
            showAlert("Faktur berhasil disimpan!");
            onFakturBaru();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menyimpan faktur: " + e.getMessage());
        }
    }

    private int getProdukIdByKode(Connection conn, String kode) throws SQLException {
        String sql = "SELECT id FROM produk WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    private boolean isFakturExist(String noFaktur) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM faktur WHERE nomor_faktur = ?")) {
            ps.setString(1, noFaktur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void onHapusFaktur(ActionEvent event) {
        String noFaktur = fieldNoFaktur.getText().trim();
        if (noFaktur.isEmpty()) {
            showAlert("Nomor faktur belum diisi.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText(null);
        confirm.setContentText("Yakin ingin menghapus faktur nomor " + noFaktur + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = Database.connect()) {
                conn.setAutoCommit(false);
                try (PreparedStatement psDetail = conn.prepareStatement("DELETE FROM detail_faktur WHERE faktur_id = (SELECT id FROM faktur WHERE nomor_faktur = ?)")) {
                    psDetail.setString(1, noFaktur);
                    psDetail.executeUpdate();
                }
                try (PreparedStatement psFaktur = conn.prepareStatement("DELETE FROM faktur WHERE nomor_faktur = ?")) {
                    psFaktur.setString(1, noFaktur);
                    int affected = psFaktur.executeUpdate();
                    if (affected == 0) {
                        conn.rollback();
                        showAlert("Faktur nomor " + noFaktur + " tidak ditemukan.");
                        return;
                    }
                }

                conn.commit();
                showAlert("Faktur berhasil dihapus.");
                onFakturBaru();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Gagal menghapus faktur: " + e.getMessage());
            }
        }
    }

    private String generateNoFakturOtomatis() {
        String prefix = "VIDA-";
        String noFakturBaru = prefix + "000001";

        String sql = "SELECT nomor_faktur FROM faktur WHERE nomor_faktur LIKE '" + prefix + "%' " +
                "ORDER BY CAST(SUBSTR(nomor_faktur, LENGTH('" + prefix + "') + 1) AS INTEGER) DESC LIMIT 1";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String terakhir = rs.getString("nomor_faktur");
                String angkaStr = terakhir.substring(prefix.length());
                int angkaTerakhir = Integer.parseInt(angkaStr);
                int angkaBaru = angkaTerakhir + 1;

                noFakturBaru = prefix + String.format("%06d", angkaBaru);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return noFakturBaru;
    }

    private void showAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
