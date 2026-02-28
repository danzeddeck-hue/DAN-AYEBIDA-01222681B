package com.studentmgmt.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility methods for safe file I/O operations.
 * All exports go to the data/ folder.
 */
public class FileHelper {

    private static final String DATA_FOLDER = "data";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private FileHelper() {}

    /**
     * Returns the data folder, creating it if needed.
     */
    public static File getDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    /**
     * Returns a timestamped file path inside data/.
     */
    public static File newDataFile(String prefix, String extension) {
        String name = prefix + "_" + LocalDateTime.now().format(TS) + "." + extension;
        return new File(getDataFolder(), name);
    }

    /**
     * Writes lines to a file, returning the file path or null on failure.
     */
    public static String writeLines(File file, Iterable<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                pw.println(line);
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            AppLogger.error("Failed to write file " + file.getPath() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Escapes a CSV field value, wrapping in quotes if necessary.
     */
    public static String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
