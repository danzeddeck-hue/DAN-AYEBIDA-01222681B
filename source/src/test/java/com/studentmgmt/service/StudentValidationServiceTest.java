package com.studentmgmt.service;

import com.studentmgmt.domain.Student;
import com.studentmgmt.domain.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentValidationService.
 * Covers all validation rules from the requirements.
 */
class StudentValidationServiceTest {

    private StudentValidationService validator;

    @BeforeEach
    void setUp() {
        validator = new StudentValidationService();
    }

    private Student validStudent() {
        return new Student("STU001", "John Kwame", "Computer Science",
                100, 3.5, "john@example.com", "0201234567", LocalDate.now(), "Active");
    }

    // ─── Student ID tests ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Valid student should pass validation")
    void testValidStudentPasses() {
        ValidationResult result = validator.validate(validStudent());
        assertTrue(result.isValid(), "Expected no validation errors for a valid student");
    }

    @Test
    @DisplayName("Student ID shorter than 4 chars should fail")
    void testStudentIdTooShort() {
        Student s = validStudent();
        s.setStudentId("AB");
        ValidationResult result = validator.validate(s);
        assertFalse(result.isValid());
        assertTrue(result.getErrorSummary().contains("4"));
    }

    @Test
    @DisplayName("Student ID longer than 20 chars should fail")
    void testStudentIdTooLong() {
        Student s = validStudent();
        s.setStudentId("ABCDE12345ABCDE12345X");
        ValidationResult result = validator.validate(s);
        assertFalse(result.isValid());
        assertTrue(result.getErrorSummary().contains("20"));
    }

    @Test
    @DisplayName("Student ID with special characters should fail")
    void testStudentIdSpecialChars() {
        Student s = validStudent();
        s.setStudentId("STU-001");
        ValidationResult result = validator.validate(s);
        assertFalse(result.isValid());
        assertTrue(result.getErrorSummary().toLowerCase().contains("letters and digits"));
    }

    @Test
    @DisplayName("Null student ID should fail")
    void testStudentIdNull() {
        Student s = validStudent();
        s.setStudentId(null);
        assertFalse(validator.validate(s).isValid());
    }

    // ─── Full name tests ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Full name with digits should fail")
    void testFullNameWithDigits() {
        Student s = validStudent();
        s.setFullName("John123 Kwame");
        ValidationResult result = validator.validate(s);
        assertFalse(result.isValid());
        assertTrue(result.getErrorSummary().toLowerCase().contains("digit"));
    }

    @Test
    @DisplayName("Full name shorter than 2 chars should fail")
    void testFullNameTooShort() {
        Student s = validStudent();
        s.setFullName("J");
        assertFalse(validator.validate(s).isValid());
    }

    // ─── Level tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Invalid level should fail")
    void testInvalidLevel() {
        Student s = validStudent();
        s.setLevel(150);
        ValidationResult result = validator.validate(s);
        assertFalse(result.isValid());
        assertTrue(result.getErrorSummary().toLowerCase().contains("level"));
    }

    @Test
    @DisplayName("Valid level 600 should pass")
    void testValidLevel600() {
        Student s = validStudent();
        s.setLevel(600);
        assertTrue(validator.validate(s).isValid());
    }

    // ─── GPA tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GPA above 4.0 should fail")
    void testGpaAboveMax() {
        Student s = validStudent();
        s.setGpa(4.1);
        assertFalse(validator.validate(s).isValid());
    }

    @Test
    @DisplayName("GPA below 0.0 should fail")
    void testGpaBelowMin() {
        Student s = validStudent();
        s.setGpa(-0.1);
        assertFalse(validator.validate(s).isValid());
    }

    @Test
    @DisplayName("GPA exactly 0.0 should pass")
    void testGpaZero() {
        Student s = validStudent();
        s.setGpa(0.0);
        assertTrue(validator.validate(s).isValid());
    }

    @Test
    @DisplayName("GPA exactly 4.0 should pass")
    void testGpaMax() {
        Student s = validStudent();
        s.setGpa(4.0);
        assertTrue(validator.validate(s).isValid());
    }

    // ─── Email tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Email without @ should fail")
    void testEmailNoAt() {
        Student s = validStudent();
        s.setEmail("johnexample.com");
        assertFalse(validator.validate(s).isValid());
    }

    @Test
    @DisplayName("Email without dot should fail")
    void testEmailNoDot() {
        Student s = validStudent();
        s.setEmail("john@examplecom");
        assertFalse(validator.validate(s).isValid());
    }

    // ─── Phone tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Phone number with letters should fail")
    void testPhoneWithLetters() {
        Student s = validStudent();
        s.setPhoneNumber("020ABCD567");
        assertFalse(validator.validate(s).isValid());
    }

    @Test
    @DisplayName("Phone number shorter than 10 digits should fail")
    void testPhoneTooShort() {
        Student s = validStudent();
        s.setPhoneNumber("123456789");
        assertFalse(validator.validate(s).isValid());
    }

    @Test
    @DisplayName("Phone number longer than 15 digits should fail")
    void testPhoneTooLong() {
        Student s = validStudent();
        s.setPhoneNumber("0123456789012345");
        assertFalse(validator.validate(s).isValid());
    }

    // ─── isValidGpa helper test ────────────────────────────────────────────────

    @Test
    @DisplayName("isValidGpa returns true for 2.5")
    void testIsValidGpa() {
        assertTrue(validator.isValidGpa(2.5));
        assertFalse(validator.isValidGpa(5.0));
        assertFalse(validator.isValidGpa(-1.0));
    }
}
