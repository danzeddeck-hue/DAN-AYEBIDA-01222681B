package com.studentmgmt.service;

import com.studentmgmt.domain.Student;
import com.studentmgmt.domain.ValidationResult;

import java.util.Set;

/**
 * Validates student field values according to the business rules.
 * Called from both the UI layer and the import layer.
 */
public class StudentValidationService {

    private static final Set<Integer> VALID_LEVELS = Set.of(100, 200, 300, 400, 500, 600, 700);

    /**
     * Validates all fields of a student.
     *
     * @param student the student to validate
     * @return a ValidationResult which is valid if no errors were found
     */
    public ValidationResult validate(Student student) {
        ValidationResult result = new ValidationResult();

        // Student ID
        if (student.getStudentId() == null || student.getStudentId().isBlank()) {
            result.addError("Student ID is required.");
        } else {
            String id = student.getStudentId().trim();
            if (id.length() < 4 || id.length() > 20) {
                result.addError("Student ID must be between 4 and 20 characters.");
            }
            if (!id.matches("[a-zA-Z0-9]+")) {
                result.addError("Student ID must contain only letters and digits.");
            }
        }

        // Full name
        if (student.getFullName() == null || student.getFullName().isBlank()) {
            result.addError("Full name is required.");
        } else {
            String name = student.getFullName().trim();
            if (name.length() < 2 || name.length() > 60) {
                result.addError("Full name must be between 2 and 60 characters.");
            }
            if (name.matches(".*\\d.*")) {
                result.addError("Full name must not contain digits.");
            }
        }

        // Programme
        if (student.getProgramme() == null || student.getProgramme().isBlank()) {
            result.addError("Programme is required.");
        }

        // Level
        if (!VALID_LEVELS.contains(student.getLevel())) {
            result.addError("Level must be one of: 100, 200, 300, 400, 500, 600, 700.");
        }

        // GPA
        if (student.getGpa() < 0.0 || student.getGpa() > 4.0) {
            result.addError("GPA must be between 0.0 and 4.0.");
        }

        // Email
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            result.addError("Email is required.");
        } else {
            String email = student.getEmail().trim();
            if (!email.contains("@") || !email.contains(".")) {
                result.addError("Email must contain '@' and '.'.");
            }
        }

        // Phone number
        if (student.getPhoneNumber() == null || student.getPhoneNumber().isBlank()) {
            result.addError("Phone number is required.");
        } else {
            String phone = student.getPhoneNumber().trim();
            if (!phone.matches("\\d+")) {
                result.addError("Phone number must contain digits only.");
            } else if (phone.length() < 10 || phone.length() > 15) {
                result.addError("Phone number must be between 10 and 15 digits.");
            }
        }

        // Status
        if (student.getStatus() == null || (!student.getStatus().equals("Active") && !student.getStatus().equals("Inactive"))) {
            result.addError("Status must be 'Active' or 'Inactive'.");
        }

        return result;
    }

    /**
     * Validates GPA value alone — useful for threshold inputs.
     */
    public boolean isValidGpa(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }
}
