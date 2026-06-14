package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.entity.Student;
import com.exam.service.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ApiResponse<PageResult<Student>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(studentService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Student> getById(@PathVariable("id") Integer id) {
        return ApiResponse.success(studentService.getById(id));
    }

    @PostMapping
    public ApiResponse<Student> create(@RequestBody Student student) {
        return ApiResponse.success(studentService.create(student));
    }

    @PutMapping("/{id}")
    public ApiResponse<Student> update(@PathVariable("id") Integer id, @RequestBody Student student) {
        student.setStudentId(id);
        return ApiResponse.success(studentService.update(student));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Integer id) {
        studentService.delete(id);
        return ApiResponse.success(null);
    }
}
