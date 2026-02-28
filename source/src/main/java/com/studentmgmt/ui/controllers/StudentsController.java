package com.studentmgmt.ui.controllers;

import com.studentmgmt.domain.Student;
import com.studentmgmt.service.StudentService;
import com.studentmgmt.ui.ServiceLocator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Students CRUD screen.
 * Delegates all business logic to StudentService.
 */
public class StudentsController implements Initializable {

    // Table
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colId, colName, colProg, colEmail, colPhone, colDate, colStatus;
    @FXML private TableColumn<Student, Integer> colLevel;
    @FXML private TableColumn<Student, Double>  colGpa;

    // Toolbar
    @FXML private TextField searchField;
    @FXML private ComboBox<String>  filterProgramme, filterStatus;
    @FXML private ComboBox<Integer> filterLevel;
    @FXML private Button btnEdit, btnDelete;

    // Form
    @FXML private VBox formPanel;
    @FXML private Label formTitle;
    @FXML private TextField fldId, fldName, fldGpa, fldEmail, fldPhone;
    @FXML private ComboBox<String>  fldProgramme, fldStatus;
    @FXML private ComboBox<Integer> fldLevel;

    private final StudentService studentService = ServiceLocator.getInstance().getStudentService();
    private boolean editMode = false;

    private static final List<Integer> LEVELS = Arrays.asList(100, 200, 300, 400, 500, 600, 700);
    private static final List<String>  STATUSES = Arrays.asList("Active", "Inactive");
    private static final List<String>  DEFAULT_PROGRAMMES = Arrays.asList(
            "Computer Science", "Information Technology", "Software Engineering",
            "Electrical Engineering", "Business Administration", "Nursing"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupFormCombos();
        setupFilterCombos();
        refreshTable();

        // Disable edit/delete when nothing selected
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            boolean sel = nw != null;
            btnEdit.setDisable(!sel);
            btnDelete.setDisable(!sel);
        });
    }

    // ─── Table setup ──────────────────────────────────────────────────────────

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colProg.setCellValueFactory(new PropertyValueFactory<>("programme"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupFormCombos() {
        fldLevel.setItems(FXCollections.observableArrayList(LEVELS));
        fldStatus.setItems(FXCollections.observableArrayList(STATUSES));
        refreshProgrammeCombos();
    }

    private void setupFilterCombos() {
        filterLevel.setItems(FXCollections.observableArrayList(LEVELS));
        filterStatus.setItems(FXCollections.observableArrayList(STATUSES));
        refreshProgrammeCombos();
    }

    private void refreshProgrammeCombos() {
        List<String> dbProgs = studentService.getAllProgrammes();
        List<String> merged  = new java.util.ArrayList<>(DEFAULT_PROGRAMMES);
        for (String p : dbProgs) { if (!merged.contains(p)) merged.add(p); }
        java.util.Collections.sort(merged);
        fldProgramme.setItems(FXCollections.observableArrayList(merged));
        filterProgramme.getItems().clear();
        filterProgramme.getItems().add("All");
        filterProgramme.getItems().addAll(merged);
    }

    // ─── Table actions ────────────────────────────────────────────────────────

    @FXML private void refreshTable() {
        studentTable.setItems(FXCollections.observableArrayList(studentService.getAllStudents()));
    }

    @FXML private void doSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { refreshTable(); return; }
        studentTable.setItems(FXCollections.observableArrayList(studentService.search(query)));
    }

    @FXML private void clearSearch() {
        searchField.clear();
        filterProgramme.setValue(null);
        filterLevel.setValue(null);
        filterStatus.setValue(null);
        refreshTable();
    }

    @FXML private void doFilter() {
        String prog   = filterProgramme.getValue();
        Integer level = filterLevel.getValue();
        String stat   = filterStatus.getValue();
        studentTable.setItems(FXCollections.observableArrayList(
                studentService.filter(prog, level, stat)));
    }

    @FXML private void sortByGpa() {
        studentTable.setItems(FXCollections.observableArrayList(studentService.sortByGpaDesc()));
    }

    @FXML private void sortByName() {
        studentTable.setItems(FXCollections.observableArrayList(studentService.sortByNameAsc()));
    }

    // ─── Form actions ─────────────────────────────────────────────────────────

    @FXML private void showAddForm() {
        editMode = false;
        formTitle.setText("Add Student");
        clearForm();
        fldId.setDisable(false);
        showForm(true);
    }

    @FXML private void showEditForm() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("No Selection", "Please select a student to edit."); return; }
        editMode = true;
        formTitle.setText("Edit Student");
        populateForm(selected);
        fldId.setDisable(true); // cannot change PK
        showForm(true);
    }

    @FXML private void cancelForm() {
        showForm(false);
    }

    @FXML private void saveStudent() {
        try {
            Student s = buildStudentFromForm();
            if (editMode) {
                studentService.updateStudent(s);
                info("Success", "Student updated successfully.");
            } else {
                studentService.addStudent(s);
                info("Success", "Student added successfully.");
            }
            showForm(false);
            refreshTable();
            refreshProgrammeCombos();
        } catch (IllegalArgumentException ex) {
            alert("Validation Error", ex.getMessage());
        } catch (Exception ex) {
            alert("Error", "An unexpected error occurred: " + ex.getMessage());
        }
    }

    @FXML private void deleteSelected() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("No Selection", "Please select a student to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete student '" + selected.getFullName() + "' (" + selected.getStudentId() + ")?\nThis cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            studentService.deleteStudent(selected.getStudentId());
            refreshTable();
        }
    }

    // ─── Form helpers ─────────────────────────────────────────────────────────

    private void showForm(boolean visible) {
        formPanel.setVisible(visible);
        formPanel.setManaged(visible);
    }

    private void clearForm() {
        fldId.clear(); fldName.clear(); fldGpa.clear();
        fldEmail.clear(); fldPhone.clear();
        fldProgramme.setValue(null);
        fldLevel.setValue(null);
        fldStatus.setValue("Active");
    }

    private void populateForm(Student s) {
        fldId.setText(s.getStudentId());
        fldName.setText(s.getFullName());
        fldProgramme.setValue(s.getProgramme());
        fldLevel.setValue(s.getLevel());
        fldGpa.setText(String.valueOf(s.getGpa()));
        fldEmail.setText(s.getEmail());
        fldPhone.setText(s.getPhoneNumber());
        fldStatus.setValue(s.getStatus());
    }

    private Student buildStudentFromForm() {
        Student s = new Student();
        s.setStudentId(fldId.getText().trim());
        s.setFullName(fldName.getText().trim());
        s.setProgramme(fldProgramme.getValue() != null ? fldProgramme.getValue().trim() : "");

        String lvlVal = fldLevel.getValue() != null ? String.valueOf(fldLevel.getValue()) : "0";
        try { s.setLevel(Integer.parseInt(lvlVal)); } catch (NumberFormatException e) { s.setLevel(0); }

        String gpaVal = fldGpa.getText().trim();
        try { s.setGpa(Double.parseDouble(gpaVal)); } catch (NumberFormatException e) { s.setGpa(-1); }

        s.setEmail(fldEmail.getText().trim());
        s.setPhoneNumber(fldPhone.getText().trim());
        s.setDateAdded(LocalDate.now());
        s.setStatus(fldStatus.getValue() != null ? fldStatus.getValue() : "Active");
        return s;
    }

    // ─── Dialog helpers ───────────────────────────────────────────────────────

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }

    private void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}
