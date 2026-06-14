package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.FillQuestion;
import com.exam.entity.JudgeQuestion;
import com.exam.entity.MultiQuestion;
import com.exam.mapper.FillQuestionMapper;
import com.exam.mapper.JudgeQuestionMapper;
import com.exam.mapper.MultiQuestionMapper;
import com.exam.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final MultiQuestionMapper multiMapper;
    private final FillQuestionMapper fillMapper;
    private final JudgeQuestionMapper judgeMapper;

    public QuestionServiceImpl(MultiQuestionMapper multiMapper,
                               FillQuestionMapper fillMapper,
                               JudgeQuestionMapper judgeMapper) {
        this.multiMapper = multiMapper;
        this.fillMapper = fillMapper;
        this.judgeMapper = judgeMapper;
    }

    // ---- 选择题 ----
    @Override
    public PageResult<MultiQuestion> listMulti(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<MultiQuestion> records = multiMapper.list(keyword, offset, size);
        return new PageResult<>(records, multiMapper.count(keyword), page, size);
    }

    @Override
    public MultiQuestion getMultiById(Integer id) {
        MultiQuestion q = multiMapper.findById(id);
        if (q == null) throw new RuntimeException("选择题不存在: " + id);
        return q;
    }

    @Override
    public MultiQuestion createMulti(MultiQuestion q) {
        multiMapper.insert(q);
        return q;
    }

    @Override
    public MultiQuestion updateMulti(MultiQuestion q) {
        getMultiById(q.getQuestionId());
        multiMapper.update(q);
        return q;
    }

    @Override
    public void deleteMulti(Integer id) {
        getMultiById(id);
        multiMapper.delete(id);
    }

    // ---- 填空题 ----
    @Override
    public PageResult<FillQuestion> listFill(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<FillQuestion> records = fillMapper.list(keyword, offset, size);
        return new PageResult<>(records, fillMapper.count(keyword), page, size);
    }

    @Override
    public FillQuestion getFillById(Integer id) {
        FillQuestion q = fillMapper.findById(id);
        if (q == null) throw new RuntimeException("填空题不存在: " + id);
        return q;
    }

    @Override
    public FillQuestion createFill(FillQuestion q) {
        fillMapper.insert(q);
        return q;
    }

    @Override
    public FillQuestion updateFill(FillQuestion q) {
        getFillById(q.getQuestionId());
        fillMapper.update(q);
        return q;
    }

    @Override
    public void deleteFill(Integer id) {
        getFillById(id);
        fillMapper.delete(id);
    }

    // ---- 判断题 ----
    @Override
    public PageResult<JudgeQuestion> listJudge(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<JudgeQuestion> records = judgeMapper.list(keyword, offset, size);
        return new PageResult<>(records, judgeMapper.count(keyword), page, size);
    }

    @Override
    public JudgeQuestion getJudgeById(Integer id) {
        JudgeQuestion q = judgeMapper.findById(id);
        if (q == null) throw new RuntimeException("判断题不存在: " + id);
        return q;
    }

    @Override
    public JudgeQuestion createJudge(JudgeQuestion q) {
        judgeMapper.insert(q);
        return q;
    }

    @Override
    public JudgeQuestion updateJudge(JudgeQuestion q) {
        getJudgeById(q.getQuestionId());
        judgeMapper.update(q);
        return q;
    }

    @Override
    public void deleteJudge(Integer id) {
        getJudgeById(id);
        judgeMapper.delete(id);
    }
}
