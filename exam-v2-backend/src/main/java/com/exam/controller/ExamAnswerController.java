package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.config.UserContext;
import com.exam.entity.ExamAnswer;
import com.exam.service.ExamAnswerService;
import com.exam.service.ScoreService;
import com.exam.vo.ExamAnswerVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-answers")
public class ExamAnswerController {

    private final ExamAnswerService examAnswerService;
    private final ScoreService scoreService;

    public ExamAnswerController(ExamAnswerService examAnswerService, ScoreService scoreService) {
        this.examAnswerService = examAnswerService;
        this.scoreService = scoreService;
    }

    /**
     * 获取指定考试的答题详情 — 学生本人查看
     */
    @GetMapping("/{examCode}")
    public ApiResponse<List<ExamAnswerVO>> getByExam(@PathVariable Integer examCode) {
        if (!UserContext.isStudent()) {
            throw new RuntimeException("仅学生可查看考试详情");
        }
        int studentId;
        try {
            studentId = Integer.parseInt(UserContext.getUserId());
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法获取学生身份");
        }
        List<ExamAnswerVO> answers = examAnswerService.getByExamAndStudent(examCode, studentId);
        return ApiResponse.success(answers);
    }

    /**
     * 教师查看指定学生在指定考试中的答题详情
     */
    @GetMapping("/{examCode}/{studentId}")
    public ApiResponse<List<ExamAnswerVO>> getByExamAndStudent(
            @PathVariable Integer examCode,
            @PathVariable Integer studentId) {
        if (!UserContext.isTeacher()) {
            throw new RuntimeException("仅教师可查看学生答题详情");
        }
        List<ExamAnswerVO> answers = examAnswerService.getByExamAndStudent(examCode, studentId);
        return ApiResponse.success(answers);
    }

    /**
     * 教师修改单条答题记录
     */
    @PutMapping("/{answerId}")
    public ApiResponse<Void> updateAnswer(@PathVariable Integer answerId,
                                          @RequestBody ExamAnswer answer) {
        if (!UserContext.isTeacher()) {
            throw new RuntimeException("仅教师可修改答题记录");
        }
        answer.setAnswerId(answerId);
        examAnswerService.updateAnswer(answer);
        return ApiResponse.success(null);
    }

    /**
     * 教师删除/重置某学生在某场考试的全部记录（答题 + 成绩）
     */
    @DeleteMapping("/{examCode}/{studentId}")
    public ApiResponse<Void> resetExamRecord(
            @PathVariable Integer examCode,
            @PathVariable Integer studentId) {
        if (!UserContext.isTeacher()) {
            throw new RuntimeException("仅教师可重置考试记录");
        }
        examAnswerService.deleteByExamAndStudent(examCode, studentId);
        scoreService.deleteByExamAndStudent(examCode, studentId);
        return ApiResponse.success(null);
    }
}
