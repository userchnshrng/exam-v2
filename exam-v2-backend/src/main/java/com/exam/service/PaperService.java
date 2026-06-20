package com.exam.service;

import com.exam.common.PageResult;
import com.exam.dto.SubmitAnswerRequest;
import com.exam.entity.Score;
import com.exam.vo.ExamQuestionVO;
import com.exam.vo.SubmitResultVO;

import java.util.List;
import java.util.Map;

public interface PaperService {

    /** 获取试卷全部题目（不含答案） */
    List<ExamQuestionVO> getPaperQuestions(Integer paperId);

    /** 提交试卷，自动判分 */
    SubmitResultVO submitAnswers(SubmitAnswerRequest request);

    /** 查询学生某次考试的答题记录 */
    List<SubmitResultVO.AnswerResult> getAnswerDetails(Integer examCode, Integer studentId);

    /** 获取组卷数据（按科目列出全部题目，标记是否已在试卷中） */
    List<Map<String, Object>> getComposeData(Integer paperId, String subject);

    /** 添加题目到试卷 */
    void addQuestionToPaper(Integer paperId, Integer questionType, Integer questionId);

    /** 从试卷移除题目 */
    void removeQuestionFromPaper(Integer paperId, Integer questionType, Integer questionId);

    /** 自动组卷：将某科目的全部题目加入试卷（不覆盖已存在映射） */
    int autoCompose(Integer paperId, String subject);
}
