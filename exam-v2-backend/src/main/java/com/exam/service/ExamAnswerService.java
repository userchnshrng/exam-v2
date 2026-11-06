package com.exam.service;

import com.exam.entity.ExamAnswer;
import com.exam.vo.ExamAnswerVO;

import java.util.List;

public interface ExamAnswerService {

    /**
     * 获取指定考试中某学生的所有答题记录（含题目题干）
     */
    List<ExamAnswerVO> getByExamAndStudent(Integer examCode, Integer studentId);

    /** 查询指定的一次交卷，不混合历史答卷。 */
    List<ExamAnswerVO> getByExamAndStudent(Integer examCode, Integer studentId, Integer scoreId);

    /**
     * 删除某学生在某场考试的全部答题记录
     */
    int deleteByExamAndStudent(Integer examCode, Integer studentId);

    /**
     * 更新单条答题记录（教师修改学生答案、判题、得分）
     */
    int updateAnswer(ExamAnswer answer);

    /**
     * AI 智能解析 — 根据答题记录生成考点与思路解析
     */
    String getAiAnalysis(Integer answerId, Integer studentId);
}
