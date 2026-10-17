package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.Score;
import java.util.List;
import java.util.Map;

public interface ScoreService {

    PageResult<Score> listByStudent(Integer studentId, int page, int size);

    Score getById(Integer scoreId);

    /** 删除一次考试成绩；仅在没有其他次成绩时清理该考试的共享答题记录。 */
    void deleteById(Integer scoreId);

    List<Map<String, Object>> listStudentExamRecords(Integer studentId);

    void deleteUnscoredAnswers(Integer examCode, Integer studentId);

    /**
     * 删除某学生在某场考试的成绩（仅管理员操作）
     */
    int deleteByExamAndStudent(Integer examCode, Integer studentId);

    /**
     * 更新成绩记录（教师修改分数）
     */
    int updateScore(Score score);
}
