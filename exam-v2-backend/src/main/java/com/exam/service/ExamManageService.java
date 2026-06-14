package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.ExamManage;

public interface ExamManageService {

    PageResult<ExamManage> list(String keyword, int page, int size);

    ExamManage getById(Integer examCode);

    ExamManage create(ExamManage exam);

    ExamManage update(ExamManage exam);

    void delete(Integer examCode);
}
