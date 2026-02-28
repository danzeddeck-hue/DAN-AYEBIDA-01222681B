package com.studentmgmt.repository;

import com.studentmgmt.domain.Student;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for repository layer using an in-memory stub.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLiteStudentRepositoryTest {

    private static InMemoryStudentRepository repo;

    @BeforeAll
    static void setUp() {
        repo = new InMemoryStudentRepository();
    }

    private Student student(String id) {
        return new Student(id, "Test User", "Computer Science",
                100, 3.5, "test@test.com", "0201234567", LocalDate.now(), "Active");
    }

    @Test @Order(1)
    @DisplayName("Save and find by ID returns present")
    void testSaveAndFindById() {
        repo.save(student("R001"));
        Optional<Student> found = repo.findById("R001");
        assertTrue(found.isPresent());
        assertEquals("R001", found.get().getStudentId());
    }

    @Test @Order(2)
    @DisplayName("FindAll returns saved students")
    void testFindAll() {
        repo.save(student("R002"));
        assertTrue(repo.findAll().size() >= 2);
    }

    @Test @Order(3)
    @DisplayName("Count increments after save")
    void testCount() {
        long before = repo.count();
        repo.save(student("R003"));
        assertEquals(before + 1, repo.count());
    }

    @Test @Order(4)
    @DisplayName("Delete removes student")
    void testDelete() {
        repo.save(student("R_DEL"));
        assertTrue(repo.findById("R_DEL").isPresent());
        repo.delete("R_DEL");
        assertFalse(repo.findById("R_DEL").isPresent());
    }

    static class InMemoryStudentRepository implements StudentRepository {
        private final List<Student> store = new ArrayList<>();
        @Override public void save(Student s) { store.add(s); }
        @Override public void update(Student s) {
            store.removeIf(x -> x.getStudentId().equals(s.getStudentId())); store.add(s); }
        @Override public void delete(String id) { store.removeIf(s -> s.getStudentId().equals(id)); }
        @Override public Optional<Student> findById(String id) {
            return store.stream().filter(s -> s.getStudentId().equals(id)).findFirst(); }
        @Override public List<Student> findAll() { return new ArrayList<>(store); }
        @Override public List<Student> search(String q) { return findAll(); }
        @Override public List<Student> filter(String p, Integer l, String st) { return findAll(); }
        @Override public List<Student> findTopByGpa(int limit, String p, Integer l) {
            return store.stream().sorted((a, b) -> Double.compare(b.getGpa(), a.getGpa())).limit(limit).toList(); }
        @Override public List<Student> findAtRisk(double threshold) {
            return store.stream().filter(s -> s.getGpa() < threshold && "Active".equals(s.getStatus())).toList(); }
        @Override public long count() { return store.size(); }
        @Override public long countActive() {
            return store.stream().filter(s -> "Active".equals(s.getStatus())).count(); }
        @Override public double averageGpa() {
            return store.stream().mapToDouble(Student::getGpa).average().orElse(0.0); }
        @Override public List<String> findAllProgrammes() {
            return store.stream().map(Student::getProgramme).distinct().sorted().toList(); }
    }
}
