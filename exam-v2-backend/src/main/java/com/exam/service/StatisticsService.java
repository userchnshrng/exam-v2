package com.exam.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    /** 成绩分布（按分段统计），可选按 examCode 过滤单场考试 */
    Map<String, Object> scoreDistribution(Integer examCode);

    /** 各考试平均分对比 */
    List<Map<String, Object>> examScoreComparison();

    /** 某考试的学生成绩排名 */
    List<Map<String, Object>> studentScoresByExam(Integer examCode);

    /** 所有可用考试列表（供筛选下拉） */
    List<Map<String, Object>> examOptions();
}
