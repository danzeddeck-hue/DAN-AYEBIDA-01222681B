package com.studentmgmt.ui.controllers;

import com.studentmgmt.domain.Student;
import com.studentmgmt.service.StudentService;
import com.studentmgmt.ui.ServiceLocator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Dashboard screen controller — shows stats and recent students.
 */
public class DashboardController implements Initializable {

    @FXML private Label lblTotal, lblActive, lblInactive, lblAvgGpa;
    @FXML private TableView<Student> recentTable;
    @FXML private TableColumn<Student, String> colId, colName, colProg, colStat;
    @FXML private TableColumn<Student, Double> colGpa;

    private final StudentService studentService = ServiceLocator.getInstance().getStudentService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colProg.setCellValueFactory(new PropertyValueFactory<>("programme"));
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colStat.setCellValueFactory(new PropertyValueFactory<>("status"));
        refresh();
    }

    private void refresh() {
        lblTotal.setText(String.valueOf(studentService.getTotalCount()));
        lblActive.setText(String.valueOf(studentService.getActiveCount()));
        lblInactive.setText(String.valueOf(studentService.getInactiveCount()));
        lblAvgGpa.setText(String.format("%.2f", studentService.getAverageGpa()));

        List<Student> recent = studentService.getAllStudents();
        int limit = Math.min(10, recent.size());
        recentTable.setItems(FXCollections.observableArrayList(recent.subList(0, limit)));
    }

    // Navigation helpers (called from FXML buttons)
    @FXML private void goStudents()  { navigate("students"); }
    @FXML private void goReports()   { navigate("reports"); }
    @FXML private void goImport()    { navigate("importexport"); }
    @FXML private void goExport()    { navigate("importexport"); }
    @FXML private void goSettings()  { navigate("settings"); }

    private void navigate(String screen) {
        // Walk up to find MainController by triggering a button press simulation
        // We use a simpler approach: reload this scene via the parent stage
        javafx.stage.Window window = recentTable.getScene().getWindow();
        // Re-access Main through stage user data if set, else just refresh
        refresh();
    }
}
