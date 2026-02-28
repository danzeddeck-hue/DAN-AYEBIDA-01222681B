# Changelog

All notable changes to Student Management System Plus are documented here.

---

## [1.0.0] – 2026-02-23

### Week 4 – Finalisation
- Completed all remaining reports: GPA Distribution and Programme Summary tabs
- Settings screen: persisted at-risk GPA threshold via Java Preferences API
- Raised unit test count to 23 (validation, reports, repository)
- Captured `mvn test` output to `evidence/test_output.txt`
- Prepared release ZIP with README, RUN_VM_OPTIONS, sample database, and sample exports
- Wrote final report

### Week 3 – Reports, Export, Import
- Built Reports screen with four tabs: Top Performers, At Risk, GPA Distribution, Programme Summary
- Implemented CSV export for all students, top performers, and at-risk reports
- Implemented CSV import with row-level validation and error reporting
- Import runs on a background thread to keep UI responsive
- Added detailed file logging: import summary, export completion, DB failures
- Added import error CSV saved to data/ folder

### Week 2 – CRUD, Validation, Search
- Completed full CRUD: add, edit, delete with confirmation dialog
- Added service-layer validation with clear error messages displayed in UI
- Search by student ID or full name
- Filter by programme, level, and status
- Sort by GPA descending and by name ascending
- Added 6 unit tests covering all major validation rules

### Week 1 – Foundation
- Created GitHub repository and pushed initial Maven project
- Designed and implemented SQLite schema with CHECK and NOT NULL constraints
- Implemented SQLiteStudentRepository with all prepared-statement queries
- Built Students screen skeleton with table, form panel, and navigation shell
- Implemented Add and View (all students) actions
- Added AppLogger and FileHelper utilities
- Initial commit with project structure and pom.xml
