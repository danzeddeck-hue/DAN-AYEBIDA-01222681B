package com.studentmgmt.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the result of a validation check.
 * Collects all errors so the UI can show them together.
 */
public class ValidationResult {

    private final List<String> errors = new ArrayList<>();

    public void addError(String message) {
        errors.add(message);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getErrorSummary() {
        return String.join("\n", errors);
    }
}
