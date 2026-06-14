package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.entity.Teacher;
import com.exam.service.TeacherService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /** 分页列表 + 搜索 */
    @GetMapping
    public ApiResponse<PageResult<Teacher>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(teacherService.list(keyword, page, size));
    }

    /** 查单条 */
    @GetMapping("/{id}")
    public ApiResponse<Teacher> getById(@PathVariable("id") Integer id) {
        return ApiResponse.success(teacherService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public ApiResponse<Teacher> create(@RequestBody Teacher teacher) {
        return ApiResponse.success(teacherService.create(teacher));
    }

    /** 更新 */
    @PutMapping("/{id}")
    public ApiResponse<Teacher> update(@PathVariable("id") Integer id, @RequestBody Teacher teacher) {
        teacher.setTeacherId(id);
        return ApiResponse.success(teacherService.update(teacher));
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Integer id) {
        teacherService.delete(id);
        return ApiResponse.success(null);
    }
}
