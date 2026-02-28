package com.studentmgmt.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the SQLite database connection and schema initialization.
 * Uses a single connection for the application lifetime.
 */
public class DatabaseManager {

    private static final String DB_FOLDER = "data";
    private static final String DB_FILE = "students.db";
    private static Connection connection;

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            File folder = new File(DB_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String url = "jdbc:sqlite:" + DB_FOLDER + File.separator + DB_FILE;
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
            initializeSchema(connection);
            AppLogger.info("Database connection established: " + url);
        }
        return connection;
    }

    private static void initializeSchema(Connection conn) throws SQLException {
        String createTable = """
                CREATE TABLE IF NOT EXISTS students (
                    student_id   TEXT PRIMARY KEY,
                    full_name    TEXT NOT NULL,
                    programme    TEXT NOT NULL,
                    level        INTEGER NOT NULL CHECK(level IN (100,200,300,400,500,600,700)),
                    gpa          REAL NOT NULL CHECK(gpa >= 0.0 AND gpa <= 4.0),
                    email        TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    date_added   TEXT NOT NULL,
                    status       TEXT NOT NULL CHECK(status IN ('Active','Inactive'))
                );
                """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
        }
        AppLogger.info("Database schema initialized.");
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                AppLogger.info("Database connection closed.");
            } catch (SQLException e) {
                AppLogger.error("Failed to close database connection: " + e.getMessage());
            }
        }
    }
}
