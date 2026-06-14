package com.exam.service.impl;

import com.exam.entity.Score;
import com.exam.mapper.ExamManageMapper;
import com.exam.mapper.ScoreMapper;
import com.exam.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ScoreMapper scoreMapper;
    private final ExamManageMapper examMapper;

    public StatisticsServiceImpl(ScoreMapper scoreMapper, ExamManageMapper examMapper) {
        this.scoreMapper = scoreMapper;
        this.examMapper = examMapper;
    }

    @Override
    public Map<String, Object> scoreDistribution(Integer examCode) {
        List<Score> all = examCode != null
                ? scoreMapper.listByExamCode(examCode)
                : scoreMapper.list(null, 0, 10000);
        int[] buckets = new int[5]; // 0-59, 60-69, 70-79, 80-89, 90-100
        String[] labels = {"0-59(不及格)", "60-69(及格)", "70-79(中)", "80-89(良)", "90-100(优)"};

        int total = 0;
        for (Score s : all) {
            int sc = s.getEtScore() != null ? s.getEtScore() : 0;
            total += sc;
            if (sc < 60) buckets[0]++;
            else if (sc < 70) buckets[1]++;
            else if (sc < 80) buckets[2]++;
            else if (sc < 90) buckets[3]++;
            else buckets[4]++;
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", labels[i]);
            item.put("value", buckets[i]);
            data.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("distribution", data);
        result.put("totalCount", all.size());
        result.put("average", all.isEmpty() ? 0 : Math.round((double) total / all.size()));
        return result;
    }

    @Override
    public List<Map<String, Object>> examScoreComparison() {
        // 直接在 SQL 层 LEFT JOIN exam_manage 完成聚合，无需 Java 侧循环 + N+1 查名称
        return scoreMapper.examComparison();
    }

    @Override
    public List<Map<String, Object>> studentScoresByExam(Integer examCode) {
        List<Score> all = scoreMapper.list(null, 0, 10000);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Score s : all) {
            if (!Objects.equals(s.getExamCode(), examCode)) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("studentId", s.getStudentId());
            item.put("score", s.getEtScore());
            item.put("subject", s.getSubject());
            item.put("date", s.getAnswerDate());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> examOptions() {
        var list = examMapper.list("", 0, 10000);
        List<Map<String, Object>> result = new ArrayList<>();
        for (var e : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("examCode", e.getExamCode());
            item.put("examName", e.getSource() + " - " + e.getDescription());
            result.add(item);
        }
        return result;
    }
}
