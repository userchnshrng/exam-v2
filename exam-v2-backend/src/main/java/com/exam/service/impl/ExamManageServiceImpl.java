package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.ExamManage;
import com.exam.mapper.ExamManageMapper;
import com.exam.service.ExamManageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamManageServiceImpl implements ExamManageService {

    private final ExamManageMapper examManageMapper;

    public ExamManageServiceImpl(ExamManageMapper examManageMapper) {
        this.examManageMapper = examManageMapper;
    }

    @Override
    public PageResult<ExamManage> list(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<ExamManage> records = examManageMapper.list(keyword, offset, size);
        long total = examManageMapper.count(keyword);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public ExamManage getById(Integer examCode) {
        ExamManage exam = examManageMapper.findById(examCode);
        if (exam == null) {
            throw new RuntimeException("考试不存在: " + examCode);
        }
        return exam;
    }

    @Override
    public ExamManage create(ExamManage exam) {
        examManageMapper.insert(exam);
        return exam;
    }

    @Override
    public ExamManage update(ExamManage exam) {
        getById(exam.getExamCode());
        examManageMapper.update(exam);
        return exam;
    }

    @Override
    public void delete(Integer examCode) {
        getById(examCode);
        examManageMapper.delete(examCode);
    }
}
