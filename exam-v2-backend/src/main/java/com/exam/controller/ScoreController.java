package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.config.UserContext;
import com.exam.entity.Score;
import com.exam.service.ScoreService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/students/{studentId}/records")
    public ApiResponse<List<Map<String, Object>>> listStudentExamRecords(@PathVariable Integer studentId) {
        if (!UserContext.isAdmin()) throw new RuntimeException("仅管理员可查看学生历史考试列表");
        return ApiResponse.success(scoreService.listStudentExamRecords(studentId));
    }

    @DeleteMapping("/students/{studentId}/unscored/{examCode}")
    public ApiResponse<Void> deleteUnscoredAnswers(@PathVariable Integer studentId, @PathVariable Integer examCode) {
        if (!UserContext.isAdmin()) throw new RuntimeException("仅管理员可清理无成绩的历史答卷");
        scoreService.deleteUnscoredAnswers(examCode, studentId);
        return ApiResponse.success(null);
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

    /**
     * 教师更新成绩
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id") Integer id, @RequestBody Score score) {
        if (!UserContext.isTeacher()) {
            throw new RuntimeException("仅教师可修改成绩");
        }
        score.setScoreId(id);
        scoreService.updateScore(score);
        return ApiResponse.success(null);
    }

    /** 仅管理员可精确删除列表中的一次考试记录。 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable("id") Integer id) {
        if (!UserContext.isAdmin()) {
            throw new RuntimeException("仅管理员可删除考试记录");
        }
        scoreService.deleteById(id);
        return ApiResponse.success(null);
    }

    /** 管理员按考试和学生清除整组成绩，供显式重置操作使用。 */
    @DeleteMapping("/by-exam/{examCode}/{studentId}")
    public ApiResponse<Void> deleteByExam(@PathVariable Integer examCode,
                                          @PathVariable Integer studentId) {
        if (!UserContext.isAdmin()) {
            throw new RuntimeException("仅管理员可删除成绩");
        }
        scoreService.deleteByExamAndStudent(examCode, studentId);
        return ApiResponse.success(null);
    }
}
