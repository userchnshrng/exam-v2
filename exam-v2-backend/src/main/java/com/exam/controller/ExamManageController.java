package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.entity.ExamManage;
import com.exam.service.ExamManageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
public class ExamManageController {

    private final ExamManageService examManageService;

    public ExamManageController(ExamManageService examManageService) {
        this.examManageService = examManageService;
    }

    @GetMapping
    public ApiResponse<PageResult<ExamManage>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(examManageService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ExamManage> getById(@PathVariable("id") Integer id) {
        return ApiResponse.success(examManageService.getById(id));
    }

    @PostMapping
    public ApiResponse<ExamManage> create(@RequestBody ExamManage exam) {
        return ApiResponse.success(examManageService.create(exam));
    }

    @PutMapping("/{id}")
    public ApiResponse<ExamManage> update(@PathVariable("id") Integer id, @RequestBody ExamManage exam) {
        exam.setExamCode(id);
        return ApiResponse.success(examManageService.update(exam));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Integer id) {
        examManageService.delete(id);
        return ApiResponse.success(null);
    }
}
