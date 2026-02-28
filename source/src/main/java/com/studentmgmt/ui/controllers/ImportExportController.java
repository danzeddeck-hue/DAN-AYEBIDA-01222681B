package com.studentmgmt.ui.controllers;

import com.studentmgmt.domain.ImportResult;
import com.studentmgmt.service.ImportExportService;
import com.studentmgmt.service.ReportService;
import com.studentmgmt.ui.ServiceLocator;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Import/Export screen.
 * Long-running operations run on background threads to avoid freezing the UI.
 */
public class ImportExportController implements Initializable {

    @FXML private Label importFileLabel, lblSuccess, lblErrors, importStatusLabel, exportStatusLabel;
    @FXML private ListView<String> errorList;

    private final ImportExportService importExportService = ServiceLocator.getInstance().getImportExportService();
    private final ReportService       reportService       = ServiceLocator.getInstance().getReportService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void importCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select CSV File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(importFileLabel.getScene().getWindow());
        if (file == null) return;

        importFileLabel.setText(file.getName());
        importStatusLabel.setText("Importing…");
        lblSuccess.setText("—");
        lblErrors.setText("—");

        // Background thread to avoid freezing UI
        Task<ImportResult> task = new Task<>() {
            @Override protected ImportResult call() { return importExportService.importFromCsv(file); }
        };
        task.setOnSucceeded(e -> {
            ImportResult result = task.getValue();
            lblSuccess.setText(String.valueOf(result.getSuccessCount()));
            lblErrors.setText(String.valueOf(result.getErrorCount()));
            importStatusLabel.setText(result.getErrorCount() == 0
                    ? "Import complete. All rows succeeded."
                    : "Import complete. " + result.getErrorCount() + " row(s) had errors — see list below.");
            errorList.setItems(FXCollections.observableArrayList(result.getErrors()));
        });
        task.setOnFailed(e -> importStatusLabel.setText("Import failed: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML private void exportAll() {
        String path = importExportService.exportAllStudents();
        exportStatusLabel.setText("All students exported to: " + path);
    }

    @FXML private void exportTopPerformers() {
        String path = importExportService.exportTopPerformers(reportService.getTopPerformers(10, null, null));
        exportStatusLabel.setText("Top performers exported to: " + path);
    }

    @FXML private void exportAtRisk() {
        String path = importExportService.exportAtRisk(reportService.getAtRiskStudents(2.0));
        exportStatusLabel.setText("At-risk students exported to: " + path);
    }
}
