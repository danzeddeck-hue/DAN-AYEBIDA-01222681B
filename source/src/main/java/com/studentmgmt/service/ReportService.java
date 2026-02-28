package com.studentmgmt.service;

import com.studentmgmt.domain.GpaBand;
import com.studentmgmt.domain.ProgrammeSummary;
import com.studentmgmt.domain.Student;
import com.studentmgmt.repository.StudentRepository;

import java.util.*;

/**
 * Business logic for report generation.
 * All calculations done here, not in controllers.
 */
public class ReportService {

    private final StudentRepository repo;

    public ReportService(StudentRepository repo) {
        this.repo = repo;
    }

    /** Returns top N students by GPA, with optional programme/level filters. */
    public List<Student> getTopPerformers(int limit, String programme, Integer level) {
        return repo.findTopByGpa(limit, programme, level);
    }

    /** Returns students whose GPA is below the given threshold. */
    public List<Student> getAtRiskStudents(double threshold) {
        return repo.findAtRisk(threshold);
    }

    /**
     * Returns GPA distribution: counts per band.
     * Bands: 0.0–1.0, 1.0–2.0, 2.0–3.0, 3.0–4.0
     */
    public List<GpaBand> getGpaDistribution() {
        List<Student> all = repo.findAll();
        int band0 = 0, band1 = 0, band2 = 0, band3 = 0;
        for (Student s : all) {
            double g = s.getGpa();
            if (g < 1.0)      band0++;
            else if (g < 2.0) band1++;
            else if (g < 3.0) band2++;
            else              band3++;
        }
        List<GpaBand> result = new ArrayList<>();
        result.add(new GpaBand("0.0 – 0.99", band0));
        result.add(new GpaBand("1.0 – 1.99", band1));
        result.add(new GpaBand("2.0 – 2.99", band2));
        result.add(new GpaBand("3.0 – 4.0",  band3));
        return result;
    }

    /**
     * Returns per-programme totals and average GPA.
     */
    public List<ProgrammeSummary> getProgrammeSummary() {
        List<Student> all = repo.findAll();
        Map<String, List<Double>> map = new LinkedHashMap<>();
        for (Student s : all) {
            map.computeIfAbsent(s.getProgramme(), k -> new ArrayList<>()).add(s.getGpa());
        }
        List<ProgrammeSummary> summaries = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : map.entrySet()) {
            List<Double> gpas = entry.getValue();
            double avg = gpas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            summaries.add(new ProgrammeSummary(entry.getKey(), gpas.size(), Math.round(avg * 100.0) / 100.0));
        }
        summaries.sort(Comparator.comparing(ProgrammeSummary::getProgramme));
        return summaries;
    }
}
