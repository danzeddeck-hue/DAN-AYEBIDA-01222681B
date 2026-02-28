package com.studentmgmt.ui;

import com.studentmgmt.repository.SQLiteStudentRepository;
import com.studentmgmt.repository.StudentRepository;
import com.studentmgmt.service.ImportExportService;
import com.studentmgmt.service.ReportService;
import com.studentmgmt.service.StudentService;
import com.studentmgmt.service.StudentValidationService;

/**
 * Simple service locator — provides shared service instances to controllers.
 * A lightweight alternative to a DI framework.
 */
public class ServiceLocator {

    private static ServiceLocator instance;

    private final StudentRepository studentRepo;
    private final StudentValidationService validationService;
    private final StudentService studentService;
    private final ReportService reportService;
    private final ImportExportService importExportService;

    private ServiceLocator() {
        studentRepo        = new SQLiteStudentRepository();
        validationService  = new StudentValidationService();
        studentService     = new StudentService(studentRepo, validationService);
        reportService      = new ReportService(studentRepo);
        importExportService = new ImportExportService(studentRepo, validationService);
    }

    public static ServiceLocator getInstance() {
        if (instance == null) instance = new ServiceLocator();
        return instance;
    }

    public StudentService getStudentService()           { return studentService; }
    public ReportService getReportService()             { return reportService; }
    public ImportExportService getImportExportService() { return importExportService; }
}
