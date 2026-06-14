package com.exam.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ExportService {

    void exportStudents(HttpServletResponse response) throws IOException;

    void exportTeachers(HttpServletResponse response) throws IOException;

    void exportScores(HttpServletResponse response, Integer examCode) throws IOException;

    void exportExams(HttpServletResponse response) throws IOException;
}
