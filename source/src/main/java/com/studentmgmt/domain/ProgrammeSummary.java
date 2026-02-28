package com.studentmgmt.domain;

/**
 * Summary statistics for a programme, used in the Programme Summary report.
 */
public class ProgrammeSummary {
    private final String programme;
    private final int totalStudents;
    private final double averageGpa;

    public ProgrammeSummary(String programme, int totalStudents, double averageGpa) {
        this.programme = programme;
        this.totalStudents = totalStudents;
        this.averageGpa = averageGpa;
    }

    public String getProgramme() { return programme; }
    public int getTotalStudents() { return totalStudents; }
    public double getAverageGpa() { return averageGpa; }
}
