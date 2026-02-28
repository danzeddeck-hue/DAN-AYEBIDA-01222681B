package com.studentmgmt.repository;

import com.studentmgmt.domain.Student;
import com.studentmgmt.util.AppLogger;
import com.studentmgmt.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SQLite implementation of StudentRepository.
 * Uses prepared statements exclusively — never string concatenation for SQL.
 */
public class SQLiteStudentRepository implements StudentRepository {

    @Override
    public void save(Student student) {
        String sql = "INSERT INTO students (student_id, full_name, programme, level, gpa, email, phone_number, date_added, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setStudentParams(ps, student);
            ps.executeUpdate();
            AppLogger.info("Student added with ID: " + student.getStudentId());
        } catch (SQLException e) {
            AppLogger.error("DB failure saving student: " + e.getMessage());
            throw new RuntimeException("Failed to save student: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Student student) {
        String sql = "UPDATE students SET full_name=?, programme=?, level=?, gpa=?, email=?, phone_number=?, date_added=?, status=? " +
                     "WHERE student_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getProgramme());
            ps.setInt(3, student.getLevel());
            ps.setDouble(4, student.getGpa());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getPhoneNumber());
            ps.setString(7, student.getDateAdded().toString());
            ps.setString(8, student.getStatus());
            ps.setString(9, student.getStudentId());
            ps.executeUpdate();
            AppLogger.info("Student updated with ID: " + student.getStudentId());
        } catch (SQLException e) {
            AppLogger.error("DB failure updating student: " + e.getMessage());
            throw new RuntimeException("Failed to update student: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.executeUpdate();
            AppLogger.info("Student deleted with ID: " + studentId);
        } catch (SQLException e) {
            AppLogger.error("DB failure deleting student: " + e.getMessage());
            throw new RuntimeException("Failed to delete student: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            AppLogger.error("DB failure finding student by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY full_name";
        return executeQuery(sql);
    }

    @Override
    public List<Student> search(String query) {
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ? ORDER BY full_name";
        String pattern = "%" + query + "%";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            return mapRows(ps.executeQuery());
        } catch (SQLException e) {
            AppLogger.error("DB failure in search: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Student> filter(String programme, Integer level, String status) {
        StringBuilder sb = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (programme != null && !programme.isEmpty() && !programme.equals("All")) {
            sb.append(" AND programme = ?");
            params.add(programme);
        }
        if (level != null) {
            sb.append(" AND level = ?");
            params.add(level);
        }
        if (status != null && !status.isEmpty() && !status.equals("All")) {
            sb.append(" AND status = ?");
            params.add(status);
        }
        sb.append(" ORDER BY full_name");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
            }
            return mapRows(ps.executeQuery());
        } catch (SQLException e) {
            AppLogger.error("DB failure in filter: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Student> findTopByGpa(int limit, String programme, Integer level) {
        StringBuilder sb = new StringBuilder("SELECT * FROM students WHERE status='Active'");
        List<Object> params = new ArrayList<>();

        if (programme != null && !programme.isEmpty() && !programme.equals("All")) {
            sb.append(" AND programme = ?");
            params.add(programme);
        }
        if (level != null) {
            sb.append(" AND level = ?");
            params.add(level);
        }
        sb.append(" ORDER BY gpa DESC LIMIT ?");
        params.add(limit);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
            }
            return mapRows(ps.executeQuery());
        } catch (SQLException e) {
            AppLogger.error("DB failure in findTopByGpa: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Student> findAtRisk(double threshold) {
        String sql = "SELECT * FROM students WHERE gpa < ? AND status='Active' ORDER BY gpa ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, threshold);
            return mapRows(ps.executeQuery());
        } catch (SQLException e) {
            AppLogger.error("DB failure in findAtRisk: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            AppLogger.error("DB failure in count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public long countActive() {
        String sql = "SELECT COUNT(*) FROM students WHERE status='Active'";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            AppLogger.error("DB failure in countActive: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double averageGpa() {
        String sql = "SELECT AVG(gpa) FROM students WHERE status='Active'";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            AppLogger.error("DB failure in averageGpa: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public List<String> findAllProgrammes() {
        String sql = "SELECT DISTINCT programme FROM students ORDER BY programme";
        List<String> programmes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                programmes.add(rs.getString(1));
            }
        } catch (SQLException e) {
            AppLogger.error("DB failure in findAllProgrammes: " + e.getMessage());
        }
        return programmes;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void setStudentParams(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.getStudentId());
        ps.setString(2, s.getFullName());
        ps.setString(3, s.getProgramme());
        ps.setInt(4, s.getLevel());
        ps.setDouble(5, s.getGpa());
        ps.setString(6, s.getEmail());
        ps.setString(7, s.getPhoneNumber());
        ps.setString(8, s.getDateAdded().toString());
        ps.setString(9, s.getStatus());
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getString("programme"),
                rs.getInt("level"),
                rs.getDouble("gpa"),
                rs.getString("email"),
                rs.getString("phone_number"),
                LocalDate.parse(rs.getString("date_added")),
                rs.getString("status")
        );
    }

    private List<Student> mapRows(ResultSet rs) throws SQLException {
        List<Student> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private List<Student> executeQuery(String sql) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return mapRows(rs);
        } catch (SQLException e) {
            AppLogger.error("DB failure in query: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
