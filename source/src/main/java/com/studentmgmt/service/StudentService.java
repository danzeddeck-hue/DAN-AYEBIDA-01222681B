package com.studentmgmt.service;

import com.studentmgmt.domain.Student;
import com.studentmgmt.domain.ValidationResult;
import com.studentmgmt.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Application service for student CRUD, search, and filtering.
 * Controllers call this service only — no SQL here.
 */
public class StudentService {

    private final StudentRepository repo;
    private final StudentValidationService validator;

    public StudentService(StudentRepository repo, StudentValidationService validator) {
        this.repo = repo;
        this.validator = validator;
    }

    /** Adds a new student after validating uniqueness and field rules. */
    public void addStudent(Student student) {
        ValidationResult result = validator.validate(student);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorSummary());
        }
        if (repo.findById(student.getStudentId()).isPresent()) {
            throw new IllegalArgumentException("Student ID '" + student.getStudentId() + "' already exists.");
        }
        repo.save(student);
    }

    /** Updates an existing student. */
    public void updateStudent(Student student) {
        ValidationResult result = validator.validate(student);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorSummary());
        }
        repo.update(student);
    }

    /** Deletes a student by ID. */
    public void deleteStudent(String studentId) {
        repo.delete(studentId);
    }

    public Optional<Student> findById(String studentId) {
        return repo.findById(studentId);
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public List<Student> search(String query) {
        return repo.search(query);
    }

    public List<Student> filter(String programme, Integer level, String status) {
        return repo.filter(programme, level, status);
    }

    /** Returns all students sorted by GPA descending. */
    public List<Student> sortByGpaDesc() {
        List<Student> list = repo.findAll();
        list.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        return list;
    }

    /** Returns all students sorted by full name ascending. */
    public List<Student> sortByNameAsc() {
        List<Student> list = repo.findAll();
        list.sort(Comparator.comparing(Student::getFullName));
        return list;
    }

    public long getTotalCount() { return repo.count(); }
    public long getActiveCount() { return repo.countActive(); }
    public long getInactiveCount() { return repo.count() - repo.countActive(); }
    public double getAverageGpa() { return repo.averageGpa(); }
    public List<String> getAllProgrammes() { return repo.findAllProgrammes(); }
}
