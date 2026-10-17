package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.Score;
import com.exam.mapper.ScoreMapper;
import com.exam.mapper.ExamAnswerMapper;
import com.exam.service.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;
    private final ExamAnswerMapper examAnswerMapper;

    public ScoreServiceImpl(ScoreMapper scoreMapper, ExamAnswerMapper examAnswerMapper) {
        this.scoreMapper = scoreMapper;
        this.examAnswerMapper = examAnswerMapper;
    }

    @Override
    @Transactional
    public void deleteById(Integer scoreId) {
        Score target = getById(scoreId);
        if (scoreMapper.deleteById(scoreId) != 1) throw new RuntimeException("该考试记录已删除，请刷新列表");
        // 历史答卷没有 scoreId，不能将共享答卷误当作本次答卷一并删除。
        if (scoreMapper.countByExamAndStudent(target.getExamCode(), target.getStudentId()) == 0) {
            examAnswerMapper.deleteByExamAndStudent(target.getExamCode(), target.getStudentId());
        }
    }

    @Override
    public List<Map<String, Object>> listStudentExamRecords(Integer studentId) {
        return scoreMapper.listStudentExamRecords(studentId);
    }

    @Override
    @Transactional
    public void deleteUnscoredAnswers(Integer examCode, Integer studentId) {
        if (scoreMapper.countByExamAndStudent(examCode, studentId) != 0) {
            throw new RuntimeException("该场考试已有成绩，请刷新后选择具体成绩记录删除");
        }
        examAnswerMapper.deleteByExamAndStudent(examCode, studentId);
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
