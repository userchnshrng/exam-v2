package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.ExamManage;
import com.exam.mapper.ExamManageMapper;
import com.exam.service.ExamManageService;
import com.exam.service.PaperService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamManageServiceImpl implements ExamManageService {

    private final ExamManageMapper examManageMapper;
    private final PaperService paperService;

    public ExamManageServiceImpl(ExamManageMapper examManageMapper,
                                  PaperService paperService) {
        this.examManageMapper = examManageMapper;
        this.paperService = paperService;
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
        exam.setSource(trimToNull(exam.getSource()));
        examManageMapper.insert(exam);
        autoComposeIfNeeded(exam);
        return exam;
    }

    @Override
    public ExamManage update(ExamManage exam) {
        getById(exam.getExamCode());
        exam.setSource(trimToNull(exam.getSource()));
        examManageMapper.update(exam);
        autoComposeIfNeeded(exam);
        return exam;
    }

    @Override
    public void delete(Integer examCode) {
        getById(examCode);
        examManageMapper.delete(examCode);
    }

    /** 自动组卷：考试设置 paperId + 课程名时，自动纳入该科目下全部题目 */
    private void autoComposeIfNeeded(ExamManage exam) {
        if (exam.getPaperId() != null && exam.getSource() != null && !exam.getSource().isEmpty()) {
            paperService.autoCompose(exam.getPaperId(), exam.getSource());
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
