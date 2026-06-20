package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.Score;

public interface ScoreService {

    PageResult<Score> listByStudent(Integer studentId, int page, int size);

    Score getById(Integer scoreId);

    /**
     * 删除某学生在某场考试的成绩（由教师操作）
     */
    int deleteByExamAndStudent(Integer examCode, Integer studentId);

    /**
     * 更新成绩记录（教师修改分数）
     */
    int updateScore(Score score);
}
