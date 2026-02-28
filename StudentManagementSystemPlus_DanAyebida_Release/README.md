# Student Management System Plus

**Developer:** Dan Ayebida  
**Version:** 1.0.0  
**Platform:** Windows (Offline)  
**Build:** Maven  
**Stack:** Java 17 · JavaFX 21 · SQLite · JDBC

---

## Overview

A fully offline desktop application that helps academic departments manage student records.  
Features include CRUD operations, search and filtering, four report types, CSV import/export, input validation, unit testing, and file-based logging.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17 or newer |
| JavaFX SDK | 21.0.1 (separate download) |
| Maven | 3.8+ |

---

## How to Run on Windows

### Option A — Run with Maven (recommended for development)

```
mvn javafx:run
```

### Option B — Run the JAR manually

1. Build the project:
   ```
   mvn clean package
   ```

2. Run with VM options:
   ```
   java --module-path "C:\javafx\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -jar target\StudentManagementSystemPlus-1.0.0.jar
   ```
   > Replace `C:\javafx\javafx-sdk-21\lib` with your actual JavaFX SDK path.

3. See `RUN_VM_OPTIONS.txt` in the release folder for the full command.

---

## Project Structure

```
StudentManagementSystemPlus/
├── pom.xml
├── README.md
├── CHANGELOG.md
├── RUN_VM_OPTIONS.txt
├── data/                          ← database, exports, logs (auto-created)
├── evidence/                      ← test output, screenshots
├── src/
│   ├── main/
│   │   ├── java/com/studentmgmt/
│   │   │   ├── domain/            Student, ValidationResult, ImportResult, GpaBand, ProgrammeSummary
│   │   │   ├── repository/        StudentRepository interface + SQLiteStudentRepository
│   │   │   ├── service/           StudentService, ReportService, ImportExportService, ValidationService
│   │   │   ├── ui/
│   │   │   │   ├── MainApp.java
│   │   │   │   ├── ServiceLocator.java
│   │   │   │   └── controllers/   One controller per screen
│   │   │   └── util/              DatabaseManager, AppLogger, FileHelper
│   │   └── resources/
│   │       ├── fxml/              Main.fxml, Dashboard.fxml, Students.fxml, Reports.fxml,
│   │       │                      ImportExport.fxml, Settings.fxml
│   │       └── styles.css
│   └── test/
│       └── java/com/studentmgmt/
│           ├── service/           StudentValidationServiceTest, ReportServiceTest
│           └── repository/        SQLiteStudentRepositoryTest
```

---

## Running Tests

```
mvn test
```

Test output is saved to `evidence/test_output.txt` during release packaging.

---

## Architecture

The project follows a clean layered design:

| Layer | Responsibility |
|-------|---------------|
| `ui` (controllers) | JavaFX views, user events; calls services only |
| `service` | Business rules, validation, reporting |
| `repository` | SQL via JDBC; all queries use prepared statements |
| `domain` | Plain Java model classes |
| `util` | Logging, file I/O helpers, DB connection |

Controllers never contain SQL. Services never talk to JDBC directly.

---

## Data

All runtime files are created inside the `data/` folder:
- `data/students.db` — SQLite database
- `data/app.log` — application log
- `data/students_export_*.csv` — full export
- `data/top_performers_export_*.csv`
- `data/at_risk_export_*.csv`
- `data/import_errors_*.csv` — import validation errors

---

## CSV Import Format

```
StudentID,FullName,Programme,Level,GPA,Email,PhoneNumber,DateAdded,Status
STU001,John Kwame,Computer Science,100,3.50,john@example.com,0201234567,2025-01-15,Active
```

Invalid rows are skipped and recorded in an error report.

---

## Academic Integrity

This project was developed individually by Dan Ayebida as part of the OOP Java Mid-Semester Assignment.  
AI tools (Claude by Anthropic) were used as a coding assistant to support architecture decisions, generate boilerplate, and review code structure. All logic, design decisions, and final code are the student's own work and responsibility.
