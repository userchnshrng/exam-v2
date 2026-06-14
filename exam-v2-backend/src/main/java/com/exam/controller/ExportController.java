package com.exam.controller;

import com.exam.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/students")
    public void exportStudents(HttpServletResponse response) throws IOException {
        exportService.exportStudents(response);
    }

    @GetMapping("/teachers")
    public void exportTeachers(HttpServletResponse response) throws IOException {
        exportService.exportTeachers(response);
    }

    @GetMapping("/scores")
    public void exportScores(@RequestParam(required = false) Integer examCode,
                             HttpServletResponse response) throws IOException {
        exportService.exportScores(response, examCode);
    }

    @GetMapping("/exams")
    public void exportExams(HttpServletResponse response) throws IOException {
        exportService.exportExams(response);
    }
}
