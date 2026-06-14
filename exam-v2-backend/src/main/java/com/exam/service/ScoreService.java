package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.Score;

public interface ScoreService {

    PageResult<Score> listByStudent(Integer studentId, int page, int size);

    Score getById(Integer scoreId);
}
