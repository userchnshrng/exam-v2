package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /** 成绩分布 */
    @GetMapping("/score-distribution")
    public ApiResponse<Map<String, Object>> scoreDistribution() {
        return ApiResponse.success(statisticsService.scoreDistribution());
    }

    /** 各考试平均分对比 */
    @GetMapping("/exam-comparison")
    public ApiResponse<List<Map<String, Object>>> examComparison() {
        return ApiResponse.success(statisticsService.examScoreComparison());
    }

    /** 某考试学生成绩 */
    @GetMapping("/exam/{examCode}/students")
    public ApiResponse<List<Map<String, Object>>> examStudents(@PathVariable Integer examCode) {
        return ApiResponse.success(statisticsService.studentScoresByExam(examCode));
    }

    /** 考试下拉选项 */
    @GetMapping("/exam-options")
    public ApiResponse<List<Map<String, Object>>> examOptions() {
        return ApiResponse.success(statisticsService.examOptions());
    }
}
