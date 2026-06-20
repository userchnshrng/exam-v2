package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.Score;
import com.exam.mapper.ScoreMapper;
import com.exam.service.ScoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;

    public ScoreServiceImpl(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }

    @Override
    public PageResult<Score> listByStudent(Integer studentId, int page, int size) {
        int offset = (page - 1) * size;
        List<Score> records = scoreMapper.list(studentId, offset, size);
        long total = scoreMapper.count(studentId);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public Score getById(Integer scoreId) {
        Score score = scoreMapper.findById(scoreId);
        if (score == null) throw new RuntimeException("成绩不存在: " + scoreId);
        return score;
    }

    @Override
    public int deleteByExamAndStudent(Integer examCode, Integer studentId) {
        return scoreMapper.deleteByExamAndStudent(examCode, studentId);
    }

    @Override
    public int updateScore(Score score) {
        return scoreMapper.update(score);
    }
}
