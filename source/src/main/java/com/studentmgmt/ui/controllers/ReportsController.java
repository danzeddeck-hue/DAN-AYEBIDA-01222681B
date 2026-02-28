package com.studentmgmt.ui.controllers;

import com.studentmgmt.domain.GpaBand;
import com.studentmgmt.domain.ProgrammeSummary;
import com.studentmgmt.domain.Student;
import com.studentmgmt.service.ImportExportService;
import com.studentmgmt.service.ReportService;
import com.studentmgmt.service.StudentService;
import com.studentmgmt.ui.ServiceLocator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Reports screen — four report tabs.
 */
public class ReportsController implements Initializable {

    // Top Performers
    @FXML private ComboBox<String>  tpProgramme;
    @FXML private ComboBox<Integer> tpLevel;
    @FXML private TableView<Student> tpTable;
    @FXML private TableColumn<Student, String>  tpId, tpName, tpProg;
    @FXML private TableColumn<Student, Integer> tpLvl;
    @FXML private TableColumn<Student, Double>  tpGpa;

    // At Risk
    @FXML private TextField arThreshold;
    @FXML private TableView<Student> arTable;
    @FXML private TableColumn<Student, String>  arId, arName, arProg;
    @FXML private TableColumn<Student, Integer> arLvl;
    @FXML private TableColumn<Student, Double>  arGpa;

    // GPA Distribution
    @FXML private TableView<GpaBand> gdTable;
    @FXML private TableColumn<GpaBand, String>  gdBand;
    @FXML private TableColumn<GpaBand, Integer> gdCount;

    // Programme Summary
    @FXML private TableView<ProgrammeSummary> psTable;
    @FXML private TableColumn<ProgrammeSummary, String>  psProg;
    @FXML private TableColumn<ProgrammeSummary, Integer> psTotal;
    @FXML private TableColumn<ProgrammeSummary, Double>  psAvg;

    private final ReportService       reportService       = ServiceLocator.getInstance().getReportService();
    private final ImportExportService importExportService = ServiceLocator.getInstance().getImportExportService();
    private final StudentService      studentService      = ServiceLocator.getInstance().getStudentService();

    private List<Student> lastTopPerformers;
    private List<Student> lastAtRisk;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTopPerformers();
        setupAtRisk();
        setupGpaDistribution();
        setupProgrammeSummary();
        populateCombos();
        runTopPerformers();
        runAtRisk();
        runGpaDistribution();
        runProgrammeSummary();
    }

    private void setupTopPerformers() {
        tpId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        tpName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tpProg.setCellValueFactory(new PropertyValueFactory<>("programme"));
        tpLvl.setCellValueFactory(new PropertyValueFactory<>("level"));
        tpGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
    }

    private void setupAtRisk() {
        arId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        arName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        arProg.setCellValueFactory(new PropertyValueFactory<>("programme"));
        arLvl.setCellValueFactory(new PropertyValueFactory<>("level"));
        arGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
    }

    private void setupGpaDistribution() {
        gdBand.setCellValueFactory(new PropertyValueFactory<>("band"));
        gdCount.setCellValueFactory(new PropertyValueFactory<>("count"));
    }

    private void setupProgrammeSummary() {
        psProg.setCellValueFactory(new PropertyValueFactory<>("programme"));
        psTotal.setCellValueFactory(new PropertyValueFactory<>("totalStudents"));
        psAvg.setCellValueFactory(new PropertyValueFactory<>("averageGpa"));
    }

    private void populateCombos() {
        List<Integer> levels = Arrays.asList(100, 200, 300, 400, 500, 600, 700);
        tpLevel.setItems(FXCollections.observableArrayList(levels));
        tpProgramme.getItems().clear();
        tpProgramme.getItems().add("All");
        tpProgramme.getItems().addAll(studentService.getAllProgrammes());
    }

    @FXML private void runTopPerformers() {
        String  prog  = tpProgramme.getValue();
        Integer level = tpLevel.getValue();
        lastTopPerformers = reportService.getTopPerformers(10, prog, level);
        tpTable.setItems(FXCollections.observableArrayList(lastTopPerformers));
    }

    @FXML private void runAtRisk() {
        double threshold = 2.0;
        try { threshold = Double.parseDouble(arThreshold.getText().trim()); }
        catch (NumberFormatException ignored) {}
        lastAtRisk = reportService.getAtRiskStudents(threshold);
        arTable.setItems(FXCollections.observableArrayList(lastAtRisk));
    }

    @FXML private void runGpaDistribution() {
        gdTable.setItems(FXCollections.observableArrayList(reportService.getGpaDistribution()));
    }

    @FXML private void runProgrammeSummary() {
        psTable.setItems(FXCollections.observableArrayList(reportService.getProgrammeSummary()));
    }

    @FXML private void exportTopPerformers() {
        if (lastTopPerformers == null || lastTopPerformers.isEmpty()) { runTopPerformers(); }
        String path = importExportService.exportTopPerformers(lastTopPerformers);
        info("Export Complete", "Top Performers exported to:\n" + path);
    }

    @FXML private void exportAtRisk() {
        if (lastAtRisk == null || lastAtRisk.isEmpty()) { runAtRisk(); }
        String path = importExportService.exportAtRisk(lastAtRisk);
        info("Export Complete", "At-Risk report exported to:\n" + path);
    }

    private void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}
