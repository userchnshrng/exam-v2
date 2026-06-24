package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.service.ImportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    /** 导入学生 */
    @PostMapping("/students")
    public ApiResponse<Map<String, Object>> importStudents(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = importService.importStudents(file.getBytes());
        return ApiResponse.success(result);
    }

    /** 导入选择题 */
    @PostMapping("/questions")
    public ApiResponse<Map<String, Object>> importQuestions(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = importService.importMultiQuestions(file.getBytes());
        return ApiResponse.success(result);
    }

    /** 导入填空题 */
    @PostMapping("/fill-questions")
    public ApiResponse<Map<String, Object>> importFillQuestions(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = importService.importFillQuestions(file.getBytes());
        return ApiResponse.success(result);
    }

    /** 导入判断题 */
    @PostMapping("/judge-questions")
    public ApiResponse<Map<String, Object>> importJudgeQuestions(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = importService.importJudgeQuestions(file.getBytes());
        return ApiResponse.success(result);
    }

    /** 导入考试 */
    @PostMapping("/exams")
    public ApiResponse<Map<String, Object>> importExams(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = importService.importExams(file.getBytes());
        return ApiResponse.success(result);
    }

    /** 导入教师 */
    @PostMapping("/teachers")
    public ApiResponse<Map<String, Object>> importTeachers(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = importService.importTeachers(file.getBytes());
        return ApiResponse.success(result);
    }
}
