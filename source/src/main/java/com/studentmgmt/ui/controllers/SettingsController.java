package com.studentmgmt.ui.controllers;

import com.studentmgmt.util.AppLogger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Settings screen — at-risk threshold and programme management.
 */
public class SettingsController implements Initializable {

    @FXML private TextField   thresholdField;
    @FXML private Label       thresholdStatus;
    @FXML private TextField   progField;
    @FXML private ListView<String> progList;

    private static final String PREF_THRESHOLD = "atRiskThreshold";
    private static final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
    private final List<String> programmes = new ArrayList<>();

    // Shared static threshold so other parts of the app can read it
    public static double atRiskThreshold = 2.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        double saved = prefs.getDouble(PREF_THRESHOLD, 2.0);
        atRiskThreshold = saved;
        thresholdField.setText(String.valueOf(saved));
        loadDefaultProgrammes();
    }

    @FXML private void saveThreshold() {
        try {
            double val = Double.parseDouble(thresholdField.getText().trim());
            if (val < 0.0 || val > 4.0) {
                thresholdStatus.setText("Threshold must be between 0.0 and 4.0.");
                return;
            }
            atRiskThreshold = val;
            prefs.putDouble(PREF_THRESHOLD, val);
            thresholdStatus.setText("Saved: " + val);
            AppLogger.info("At-risk threshold updated to " + val);
        } catch (NumberFormatException e) {
            thresholdStatus.setText("Invalid number.");
        }
    }

    @FXML private void addProgramme() {
        String name = progField.getText().trim();
        if (name.isEmpty()) return;
        if (!programmes.contains(name)) {
            programmes.add(name);
            progList.setItems(FXCollections.observableArrayList(programmes));
            progField.clear();
        }
    }

    @FXML private void removeProgramme() {
        String selected = progList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            programmes.remove(selected);
            progList.setItems(FXCollections.observableArrayList(programmes));
        }
    }

    private void loadDefaultProgrammes() {
        programmes.addAll(List.of(
                "Computer Science", "Information Technology", "Software Engineering",
                "Electrical Engineering", "Business Administration", "Nursing"
        ));
        progList.setItems(FXCollections.observableArrayList(programmes));
    }
}
