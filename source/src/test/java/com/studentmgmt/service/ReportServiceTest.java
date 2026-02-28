package com.studentmgmt.service;

import com.studentmgmt.domain.GpaBand;
import com.studentmgmt.domain.ProgrammeSummary;
import com.studentmgmt.domain.Student;
import com.studentmgmt.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReportService.
 * Uses an in-memory stub repository to avoid a real database.
 */
class ReportServiceTest {

    private ReportService reportService;
    private StubStudentRepository stub;

    @BeforeEach
    void setUp() {
        stub = new StubStudentRepository();
        reportService = new ReportService(stub);
        loadTestData();
    }

    private void loadTestData() {
        stub.add(student("S001", "Alice Mensa",    "CS",  300, 3.9, "Active"));
        stub.add(student("S002", "Bob Yaw",         "CS",  200, 1.5, "Active"));
        stub.add(student("S003", "Carol Ama",       "IT",  400, 2.8, "Active"));
        stub.add(student("S004", "Dan Kofi",        "IT",  100, 0.8, "Active"));
        stub.add(student("S005", "Eve Abena",       "CS",  300, 3.5, "Active"));
        stub.add(student("S006", "Frank Kwesi",     "Eng", 500, 3.1, "Active"));
        stub.add(student("S007", "Grace Adjoa",     "CS",  100, 1.8, "Active"));
        stub.add(student("S008", "Henry Kojo",      "IT",  200, 2.2, "Inactive"));
        stub.add(student("S009", "Irene Akosua",    "Eng", 600, 3.8, "Active"));
        stub.add(student("S010", "James Fiifi",     "CS",  400, 2.0, "Active"));
        stub.add(student("S011", "Kate Esi",        "IT",  300, 1.1, "Active"));
        stub.add(student("S012", "Leo Kwame",       "Eng", 200, 3.3, "Active"));
    }

    private Student student(String id, String name, String prog, int level, double gpa, String status) {
        return new Student(id, name, prog, level, gpa, id + "@test.com", "0201234567", LocalDate.now(), status);
    }

    @Test
    @DisplayName("GPA distribution: band 0–0.99 contains correct count")
    void testGpaDistributionBand0() {
        List<GpaBand> dist = reportService.getGpaDistribution();
        GpaBand band = dist.get(0);
        assertEquals("0.0 – 0.99", band.getBand());
        assertEquals(1, band.getCount()); // Dan Kofi 0.8
    }

    @Test
    @DisplayName("GPA distribution: band 1–1.99 correct count")
    void testGpaDistributionBand1() {
        List<GpaBand> dist = reportService.getGpaDistribution();
        assertEquals(3, dist.get(1).getCount()); // Bob 1.5, Grace 1.8, Kate 1.1
    }

    @Test
    @DisplayName("GPA distribution: band 2–2.99 correct count")
    void testGpaDistributionBand2() {
        List<GpaBand> dist = reportService.getGpaDistribution();
        assertEquals(3, dist.get(2).getCount()); // Carol 2.8, Henry 2.2, James 2.0
    }

    @Test
    @DisplayName("GPA distribution: band 3–4.0 correct count")
    void testGpaDistributionBand3() {
        List<GpaBand> dist = reportService.getGpaDistribution();
        assertEquals(5, dist.get(3).getCount()); // Alice 3.9, Eve 3.5, Frank 3.1, Irene 3.8, Leo 3.3
    }

    @Test
    @DisplayName("GPA distribution returns exactly 4 bands")
    void testGpaDistributionFourBands() {
        assertEquals(4, reportService.getGpaDistribution().size());
    }

    @Test
    @DisplayName("Programme summary contains all 3 programmes")
    void testProgrammeSummaryCount() {
        List<ProgrammeSummary> summary = reportService.getProgrammeSummary();
        assertEquals(3, summary.size());
    }

    @Test
    @DisplayName("CS programme average GPA is correct")
    void testProgrammeSummaryAvgGpa() {
        List<ProgrammeSummary> summary = reportService.getProgrammeSummary();
        ProgrammeSummary cs = summary.stream()
                .filter(p -> p.getProgramme().equals("CS"))
                .findFirst()
                .orElseThrow();
        // CS students: 3.9, 1.5, 3.5, 1.8, 2.0 → avg = 12.7 / 5 = 2.54
        assertEquals(5, cs.getTotalStudents());
        assertEquals(2.54, cs.getAverageGpa(), 0.01);
    }

    @Test
    @DisplayName("At-risk students below 2.0 are returned correctly")
    void testAtRiskBelowThreshold() {
        List<Student> atRisk = reportService.getAtRiskStudents(2.0);
        // Bob 1.5, Dan 0.8, Grace 1.8, Kate 1.1 — all Active and < 2.0
        assertTrue(atRisk.stream().allMatch(s -> s.getGpa() < 2.0));
        assertEquals(4, atRisk.size());
    }

    @Test
    @DisplayName("At-risk does not include Inactive students")
    void testAtRiskExcludesInactive() {
        // Henry (S008) is Inactive with GPA 2.2 — should not appear even with threshold 3.0
        List<Student> atRisk = reportService.getAtRiskStudents(3.0);
        assertTrue(atRisk.stream().noneMatch(s -> s.getStudentId().equals("S008")));
    }

    // ─── Stub repository ──────────────────────────────────────────────────────

    static class StubStudentRepository implements StudentRepository {
        private final List<Student> store = new ArrayList<>();
        void add(Student s) { store.add(s); }

        @Override public void save(Student s)     { store.add(s); }
        @Override public void update(Student s)   {}
        @Override public void delete(String id)   { store.removeIf(s -> s.getStudentId().equals(id)); }
        @Override public Optional<Student> findById(String id) {
            return store.stream().filter(s -> s.getStudentId().equals(id)).findFirst(); }
        @Override public List<Student> findAll()  { return new ArrayList<>(store); }
        @Override public List<Student> search(String q) { return findAll(); }
        @Override public List<Student> filter(String p, Integer l, String st) { return findAll(); }
        @Override public List<Student> findTopByGpa(int limit, String prog, Integer level) {
            return store.stream()
                    .filter(s -> s.getStatus().equals("Active"))
                    .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                    .limit(limit)
                    .toList();
        }
        @Override public List<Student> findAtRisk(double threshold) {
            return store.stream()
                    .filter(s -> s.getStatus().equals("Active") && s.getGpa() < threshold)
                    .toList();
        }
        @Override public long count()       { return store.size(); }
        @Override public long countActive() { return store.stream().filter(s -> s.getStatus().equals("Active")).count(); }
        @Override public double averageGpa() {
            return store.stream().filter(s -> s.getStatus().equals("Active"))
                    .mapToDouble(Student::getGpa).average().orElse(0.0); }
        @Override public List<String> findAllProgrammes() {
            return store.stream().map(Student::getProgramme).distinct().sorted().toList(); }
    }
}
