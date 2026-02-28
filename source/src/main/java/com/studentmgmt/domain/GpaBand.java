package com.studentmgmt.domain;

/**
 * Represents a GPA band entry for the GPA distribution report.
 */
public class GpaBand {
    private final String band;
    private final int count;

    public GpaBand(String band, int count) {
        this.band = band;
        this.count = count;
    }

    public String getBand() { return band; }
    public int getCount() { return count; }
}
