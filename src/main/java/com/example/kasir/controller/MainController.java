package com.example.kasir.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.kasir.Database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private Label infoLabel;

    @FXML
    private VBox sidebar;

    @FXML
    private StackPane contentArea;

    public void initialize() {
        tampilkanInfoAplikasi();
    }

    private void tampilkanInfoAplikasi() {
        String sql = "SELECT COUNT(*) AS total_produk FROM produk";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total_produk");
                infoLabel.setText("Total produk: " + total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showHome(ActionEvent event) {
        System.out.println("showHome dipanggil");
        loadContent("/fxml/home.fxml");
    }

    @FXML
    private void showProduk(ActionEvent event) {
        System.out.println("showProduk dipanggil");
        loadContent("/fxml/produk.fxml");
    }

    @FXML
    private void showHistory(ActionEvent event) {
        System.out.println("showHistory dipanggil");
        loadContent("/fxml/history.fxml");
    }

    @FXML
    private void toggleSidebar(ActionEvent event) {
        System.out.println("toggleSidebar dipanggil");
        if (sidebar.isVisible()) {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
        } else {
            sidebar.setVisible(true);
            sidebar.setManaged(true);
        }
    }

    /**
     * Load dan tampilkan file FXML di contentArea (StackPane)
     * @param fxmlPath path relatif file FXML dari resources (misal "/fxml/home.fxml")
     */
    private void loadContent(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
