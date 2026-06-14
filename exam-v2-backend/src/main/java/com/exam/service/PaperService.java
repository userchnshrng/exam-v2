package com.exam.service;

import com.exam.common.PageResult;
import com.exam.dto.SubmitAnswerRequest;
import com.exam.entity.Score;
import com.exam.vo.ExamQuestionVO;
import com.exam.vo.SubmitResultVO;

import java.util.List;

public interface PaperService {

    /** 获取试卷全部题目（不含答案） */
    List<ExamQuestionVO> getPaperQuestions(Integer paperId);

    /** 提交试卷，自动判分 */
    SubmitResultVO submitAnswers(SubmitAnswerRequest request);

    /** 查询学生某次考试的答题记录 */
    List<SubmitResultVO.AnswerResult> getAnswerDetails(Integer examCode, Integer studentId);
}
