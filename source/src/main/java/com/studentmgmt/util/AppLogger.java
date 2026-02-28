package com.studentmgmt.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple file-based logger.
 * Logs to data/app.log.
 * Does NOT log full personal record data.
 */
public class AppLogger {

    private static final String LOG_FOLDER = "data";
    private static final String LOG_FILE = "app.log";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static PrintWriter writer;

    static {
        try {
            File folder = new File(LOG_FOLDER);
            if (!folder.exists()) folder.mkdirs();
            writer = new PrintWriter(new FileWriter(LOG_FOLDER + File.separator + LOG_FILE, true));
        } catch (IOException e) {
            System.err.println("Could not open log file: " + e.getMessage());
        }
    }

    private AppLogger() {}

    public static synchronized void info(String message) {
        log("INFO", message);
    }

    public static synchronized void warn(String message) {
        log("WARN", message);
    }

    public static synchronized void error(String message) {
        log("ERROR", message);
    }

    private static void log(String level, String message) {
        String line = "[" + LocalDateTime.now().format(FMT) + "] [" + level + "] " + message;
        System.out.println(line);
        if (writer != null) {
            writer.println(line);
            writer.flush();
        }
    }

    public static void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
