package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.config.UserContext;
import com.exam.entity.Score;
import com.exam.service.ScoreService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping
    public ApiResponse<PageResult<Score>> list(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 权限过滤：学生只能看自己的成绩
        if (UserContext.isStudent()) {
            try {
                studentId = Integer.parseInt(UserContext.getUserId());
            } catch (NumberFormatException ignored) {}
        }

        return ApiResponse.success(scoreService.listByStudent(studentId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Score> getById(@PathVariable("id") Integer id) {
        Score score = scoreService.getById(id);
        // 学生只能看自己的成绩
        if (UserContext.isStudent() && score.getStudentId() != null) {
            try {
                int myId = Integer.parseInt(UserContext.getUserId());
                if (score.getStudentId() != myId) {
                    throw new RuntimeException("无权查看他人成绩");
                }
            } catch (NumberFormatException ignored) {}
        }
        return ApiResponse.success(score);
    }
}
