package com.studentmgmt.service;

import com.studentmgmt.domain.*;
import com.studentmgmt.repository.StudentRepository;
import com.studentmgmt.util.AppLogger;
import com.studentmgmt.util.FileHelper;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CSV import and export for student records and reports.
 */
public class ImportExportService {

    private static final String CSV_HEADER =
            "StudentID,FullName,Programme,Level,GPA,Email,PhoneNumber,DateAdded,Status";

    private final StudentRepository repo;
    private final StudentValidationService validator;

    public ImportExportService(StudentRepository repo, StudentValidationService validator) {
        this.repo = repo;
        this.validator = validator;
    }

    // ─── Export ───────────────────────────────────────────────────────────────

    /** Exports all students to a CSV file inside data/. Returns the file path. */
    public String exportAllStudents() {
        List<Student> students = repo.findAll();
        List<String> lines = buildStudentCsvLines(students);
        File file = FileHelper.newDataFile("students_export", "csv");
        String path = FileHelper.writeLines(file, lines);
        AppLogger.info("Exported " + students.size() + " students to " + path);
        return path;
    }

    /** Exports top performers report to CSV. */
    public String exportTopPerformers(List<Student> students) {
        List<String> lines = buildStudentCsvLines(students);
        File file = FileHelper.newDataFile("top_performers_export", "csv");
        String path = FileHelper.writeLines(file, lines);
        AppLogger.info("Exported top performers (" + students.size() + ") to " + path);
        return path;
    }

    /** Exports at-risk report to CSV. */
    public String exportAtRisk(List<Student> students) {
        List<String> lines = buildStudentCsvLines(students);
        File file = FileHelper.newDataFile("at_risk_export", "csv");
        String path = FileHelper.writeLines(file, lines);
        AppLogger.info("Exported at-risk students (" + students.size() + ") to " + path);
        return path;
    }

    // ─── Import ───────────────────────────────────────────────────────────────

    /**
     * Imports students from a CSV file.
     * Invalid rows are skipped and recorded.
     * Duplicate IDs are rejected.
     * Returns an ImportResult with counts and error messages.
     */
    public ImportResult importFromCsv(File file) {
        ImportResult result = new ImportResult();
        int row = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                row++;
                line = line.trim();
                if (firstLine) {
                    firstLine = false;
                    // Skip header line
                    if (line.toLowerCase().startsWith("studentid") || line.toLowerCase().startsWith("student_id")) {
                        continue;
                    }
                }
                if (line.isEmpty()) continue;

                String[] parts = splitCsv(line);
                if (parts.length < 9) {
                    result.addError("Row " + row + ": Not enough columns (expected 9, found " + parts.length + ").");
                    continue;
                }

                try {
                    Student student = parseStudent(parts);
                    ValidationResult vr = validator.validate(student);
                    if (!vr.isValid()) {
                        result.addError("Row " + row + " [" + parts[0] + "]: " + vr.getErrorSummary());
                        continue;
                    }
                    if (repo.findById(student.getStudentId()).isPresent()) {
                        result.addError("Row " + row + ": Duplicate student ID '" + student.getStudentId() + "' rejected.");
                        continue;
                    }
                    repo.save(student);
                    result.incrementSuccess();
                } catch (Exception e) {
                    result.addError("Row " + row + ": Parse error – " + e.getMessage());
                }
            }
        } catch (IOException e) {
            AppLogger.error("Import failed: " + e.getMessage());
            result.addError("File read error: " + e.getMessage());
        }

        AppLogger.info("Import summary: " + result.getSuccessCount() + " succeeded, " + result.getErrorCount() + " errors.");

        // Save error report
        if (!result.getErrors().isEmpty()) {
            File errorFile = FileHelper.newDataFile("import_errors", "csv");
            FileHelper.writeLines(errorFile, result.getErrors());
            AppLogger.info("Import errors saved to " + errorFile.getPath());
        }

        return result;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private List<String> buildStudentCsvLines(List<Student> students) {
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (Student s : students) {
            lines.add(String.join(",",
                    FileHelper.csvEscape(s.getStudentId()),
                    FileHelper.csvEscape(s.getFullName()),
                    FileHelper.csvEscape(s.getProgramme()),
                    String.valueOf(s.getLevel()),
                    String.format("%.2f", s.getGpa()),
                    FileHelper.csvEscape(s.getEmail()),
                    FileHelper.csvEscape(s.getPhoneNumber()),
                    s.getDateAdded().toString(),
                    s.getStatus()
            ));
        }
        return lines;
    }

    private Student parseStudent(String[] parts) {
        Student s = new Student();
        s.setStudentId(parts[0].trim());
        s.setFullName(parts[1].trim());
        s.setProgramme(parts[2].trim());
        s.setLevel(Integer.parseInt(parts[3].trim()));
        s.setGpa(Double.parseDouble(parts[4].trim()));
        s.setEmail(parts[5].trim());
        s.setPhoneNumber(parts[6].trim());
        s.setDateAdded(LocalDate.parse(parts[7].trim()));
        s.setStatus(parts[8].trim());
        return s;
    }

    /** Simple CSV split that handles quoted fields. */
    private String[] splitCsv(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}
