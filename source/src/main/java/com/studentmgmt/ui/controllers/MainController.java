package com.studentmgmt.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls the main shell: top navigation and content area swapping.
 */
public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    @FXML private ToggleButton btnDashboard, btnStudents, btnReports, btnImportExport, btnSettings;

    private ToggleGroup navGroup;
    private Node dashNode, studentsNode, reportsNode, importExportNode, settingsNode;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navGroup = new ToggleGroup();
        btnDashboard.setToggleGroup(navGroup);
        btnStudents.setToggleGroup(navGroup);
        btnReports.setToggleGroup(navGroup);
        btnImportExport.setToggleGroup(navGroup);
        btnSettings.setToggleGroup(navGroup);

        // Prevent deselecting
        navGroup.selectedToggleProperty().addListener((obs, old, nw) -> {
            if (nw == null && old != null) old.setSelected(true);
        });

        showDashboard();
    }

    @FXML private void showDashboard() {
        setContent(getDashboard());
        btnDashboard.setSelected(true);
        setStatus("Dashboard");
    }

    @FXML private void showStudents() {
        setContent(getStudents());
        btnStudents.setSelected(true);
        setStatus("Students");
    }

    @FXML private void showReports() {
        setContent(getReports());
        btnReports.setSelected(true);
        setStatus("Reports");
    }

    @FXML private void showImportExport() {
        setContent(getImportExport());
        btnImportExport.setSelected(true);
        setStatus("Import / Export");
    }

    @FXML private void showSettings() {
        setContent(getSettings());
        btnSettings.setSelected(true);
        setStatus("Settings");
    }

    // Called by child controllers to navigate programmatically
    public static MainController instance;

    public void navigateTo(String screen) {
        switch (screen) {
            case "students"     -> showStudents();
            case "reports"      -> showReports();
            case "importexport" -> showImportExport();
            case "settings"     -> showSettings();
            default             -> showDashboard();
        }
    }

    private void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    // Lazy-load screens

    private Node getDashboard() {
        if (dashNode == null) dashNode = load("/fxml/Dashboard.fxml");
        return dashNode;
    }

    private Node getStudents() {
        if (studentsNode == null) studentsNode = load("/fxml/Students.fxml");
        return studentsNode;
    }

    private Node getReports() {
        if (reportsNode == null) reportsNode = load("/fxml/Reports.fxml");
        return reportsNode;
    }

    private Node getImportExport() {
        if (importExportNode == null) importExportNode = load("/fxml/ImportExport.fxml");
        return importExportNode;
    }

    private Node getSettings() {
        if (settingsNode == null) settingsNode = load("/fxml/Settings.fxml");
        return settingsNode;
    }

    private Node load(String fxml) {
        try {
            URL url = getClass().getResource(fxml);
            if (url == null) throw new RuntimeException("FXML not found: " + fxml);
            return FXMLLoader.load(url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }
}
