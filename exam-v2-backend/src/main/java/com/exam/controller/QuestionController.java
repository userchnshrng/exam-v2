package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.entity.FillQuestion;
import com.exam.entity.JudgeQuestion;
import com.exam.entity.MultiQuestion;
import com.exam.service.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // ==================== 选择题 ====================

    @GetMapping("/multi")
    public ApiResponse<PageResult<MultiQuestion>> listMulti(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(questionService.listMulti(keyword, page, size));
    }

    @GetMapping("/multi/{id}")
    public ApiResponse<MultiQuestion> getMulti(@PathVariable("id") Integer id) {
        return ApiResponse.success(questionService.getMultiById(id));
    }

    @PostMapping("/multi")
    public ApiResponse<MultiQuestion> createMulti(@RequestBody MultiQuestion q) {
        return ApiResponse.success(questionService.createMulti(q));
    }

    @PutMapping("/multi/{id}")
    public ApiResponse<MultiQuestion> updateMulti(@PathVariable("id") Integer id, @RequestBody MultiQuestion q) {
        q.setQuestionId(id);
        return ApiResponse.success(questionService.updateMulti(q));
    }

    @DeleteMapping("/multi/{id}")
    public ApiResponse<Void> deleteMulti(@PathVariable("id") Integer id) {
        questionService.deleteMulti(id);
        return ApiResponse.success(null);
    }

    // ==================== 填空题 ====================

    @GetMapping("/fill")
    public ApiResponse<PageResult<FillQuestion>> listFill(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(questionService.listFill(keyword, page, size));
    }

    @GetMapping("/fill/{id}")
    public ApiResponse<FillQuestion> getFill(@PathVariable("id") Integer id) {
        return ApiResponse.success(questionService.getFillById(id));
    }

    @PostMapping("/fill")
    public ApiResponse<FillQuestion> createFill(@RequestBody FillQuestion q) {
        return ApiResponse.success(questionService.createFill(q));
    }

    @PutMapping("/fill/{id}")
    public ApiResponse<FillQuestion> updateFill(@PathVariable("id") Integer id, @RequestBody FillQuestion q) {
        q.setQuestionId(id);
        return ApiResponse.success(questionService.updateFill(q));
    }

    @DeleteMapping("/fill/{id}")
    public ApiResponse<Void> deleteFill(@PathVariable("id") Integer id) {
        questionService.deleteFill(id);
        return ApiResponse.success(null);
    }

    // ==================== 判断题 ====================

    @GetMapping("/judge")
    public ApiResponse<PageResult<JudgeQuestion>> listJudge(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(questionService.listJudge(keyword, page, size));
    }

    @GetMapping("/judge/{id}")
    public ApiResponse<JudgeQuestion> getJudge(@PathVariable("id") Integer id) {
        return ApiResponse.success(questionService.getJudgeById(id));
    }

    @PostMapping("/judge")
    public ApiResponse<JudgeQuestion> createJudge(@RequestBody JudgeQuestion q) {
        return ApiResponse.success(questionService.createJudge(q));
    }

    @PutMapping("/judge/{id}")
    public ApiResponse<JudgeQuestion> updateJudge(@PathVariable("id") Integer id, @RequestBody JudgeQuestion q) {
        q.setQuestionId(id);
        return ApiResponse.success(questionService.updateJudge(q));
    }

    @DeleteMapping("/judge/{id}")
    public ApiResponse<Void> deleteJudge(@PathVariable("id") Integer id) {
        questionService.deleteJudge(id);
        return ApiResponse.success(null);
    }
}
