package com.studentmgmt.repository;

import com.studentmgmt.domain.Student;
import java.util.List;
import java.util.Optional;

/**
 * Contract for all student data access operations.
 * Implementations must use prepared statements only.
 */
public interface StudentRepository {

    void save(Student student);

    void update(Student student);

    void delete(String studentId);

    Optional<Student> findById(String studentId);

    List<Student> findAll();

    List<Student> search(String query);

    List<Student> filter(String programme, Integer level, String status);

    List<Student> findTopByGpa(int limit, String programme, Integer level);

    List<Student> findAtRisk(double threshold);

    long count();

    long countActive();

    double averageGpa();

    List<String> findAllProgrammes();
}
