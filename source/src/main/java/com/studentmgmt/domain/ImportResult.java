package com.studentmgmt.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary of a CSV import operation.
 */
public class ImportResult {
    private int successCount;
    private final List<String> errors = new ArrayList<>();

    public void incrementSuccess() { successCount++; }
    public void addError(String message) { errors.add(message); }

    public int getSuccessCount() { return successCount; }
    public int getErrorCount() { return errors.size(); }
    public List<String> getErrors() { return errors; }
}
