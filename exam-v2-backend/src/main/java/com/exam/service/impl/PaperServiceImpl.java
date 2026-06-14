package com.exam.service.impl;

import com.exam.dto.SubmitAnswerRequest;
import com.exam.entity.ExamAnswer;
import com.exam.entity.ExamManage;
import com.exam.entity.Score;
import com.exam.mapper.*;
import com.exam.service.PaperService;
import com.exam.vo.ExamQuestionVO;
import com.exam.vo.SubmitResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PaperServiceImpl implements PaperService {

    private final PaperManageMapper paperManageMapper;
    private final ExamManageMapper examManageMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final ScoreMapper scoreMapper;

    public PaperServiceImpl(PaperManageMapper paperManageMapper,
                            ExamManageMapper examManageMapper,
                            ExamAnswerMapper examAnswerMapper,
                            ScoreMapper scoreMapper) {
        this.paperManageMapper = paperManageMapper;
        this.examManageMapper = examManageMapper;
        this.examAnswerMapper = examAnswerMapper;
        this.scoreMapper = scoreMapper;
    }

    @Override
    public List<ExamQuestionVO> getPaperQuestions(Integer paperId) {
        List<Map<String, Object>> mappings = paperManageMapper.listByPaperId(paperId);
        List<ExamQuestionVO> questions = new ArrayList<>();

        for (Map<String, Object> row : mappings) {
            Integer questionType = (Integer) row.get("questionType");
            Integer questionId = (Integer) row.get("questionId");

            Map<String, Object> q = null;
            if (questionType == 1) {
                q = paperManageMapper.findMultiById(questionId);
            } else if (questionType == 2) {
                q = paperManageMapper.findFillById(questionId);
            } else if (questionType == 3) {
                q = paperManageMapper.findJudgeById(questionId);
            }
            if (q == null) continue;

            ExamQuestionVO vo = toQuestionVO(questionType, q);
            questions.add(vo);
        }
        return questions;
    }

    @Override
    @Transactional
    public SubmitResultVO submitAnswers(SubmitAnswerRequest request) {
        ExamManage exam = examManageMapper.findById(request.getExamCode());
        if (exam == null) throw new RuntimeException("考试不存在");

        // 获取试卷题目（用于对比答案）
        List<Map<String, Object>> mappings = paperManageMapper.listByPaperId(exam.getPaperId());

        int totalScore = 0;
        int correctCount = 0;
        List<SubmitResultVO.AnswerResult> details = new ArrayList<>();

        for (SubmitAnswerRequest.AnswerItem item : request.getAnswers()) {
            String correctAnswer = findCorrectAnswer(mappings, item.getQuestionId(), item.getQuestionType());
            boolean isCorrect = correctAnswer != null && normalize(item.getAnswer()).equalsIgnoreCase(normalize(correctAnswer));
            int questionScore = findQuestionScore(mappings, item.getQuestionId(), item.getQuestionType());

            if (isCorrect) {
                correctCount++;
                totalScore += questionScore;
            }

            // 保存答题记录
            ExamAnswer answer = new ExamAnswer();
            answer.setExamCode(request.getExamCode());
            answer.setStudentId(request.getStudentId());
            answer.setQuestionType(item.getQuestionType());
            answer.setQuestionId(item.getQuestionId());
            answer.setStudentAnswer(item.getAnswer());
            answer.setCorrectAnswer(correctAnswer);
            answer.setIsCorrect(isCorrect);
            answer.setScore(isCorrect ? questionScore : 0);
            examAnswerMapper.insert(answer);

            SubmitResultVO.AnswerResult detail = new SubmitResultVO.AnswerResult();
            detail.setQuestionId(item.getQuestionId());
            detail.setQuestionType(item.getQuestionType());
            detail.setStudentAnswer(item.getAnswer());
            detail.setCorrectAnswer(correctAnswer);
            detail.setIsCorrect(isCorrect);
            detail.setScore(isCorrect ? questionScore : 0);
            details.add(detail);
        }

        // 保存总分
        Score score = new Score();
        score.setExamCode(request.getExamCode());
        score.setStudentId(request.getStudentId());
        score.setSubject(exam.getSource());
        score.setEtScore(totalScore);
        score.setAnswerDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        scoreMapper.insert(score);

        SubmitResultVO result = new SubmitResultVO();
        result.setTotalScore(totalScore);
        result.setCorrectCount(correctCount);
        result.setTotalCount(details.size());
        result.setDetails(details);
        return result;
    }

    @Override
    public List<SubmitResultVO.AnswerResult> getAnswerDetails(Integer examCode, Integer studentId) {
        List<ExamAnswer> answers = examAnswerMapper.findByExamAndStudent(examCode, studentId);
        List<SubmitResultVO.AnswerResult> results = new ArrayList<>();
        for (ExamAnswer a : answers) {
            SubmitResultVO.AnswerResult r = new SubmitResultVO.AnswerResult();
            r.setQuestionId(a.getQuestionId());
            r.setQuestionType(a.getQuestionType());
            r.setStudentAnswer(a.getStudentAnswer());
            r.setCorrectAnswer(a.getCorrectAnswer());
            r.setIsCorrect(a.getIsCorrect());
            r.setScore(a.getScore());
            results.add(r);
        }
        return results;
    }

    // ---- 辅助方法 ----

    private ExamQuestionVO toQuestionVO(Integer questionType, Map<String, Object> row) {
        ExamQuestionVO vo = new ExamQuestionVO();
        vo.setQuestionType(questionType);
        vo.setQuestionId((Integer) row.get("questionId"));
        vo.setSubject(str(row, "subject"));
        vo.setQuestion(str(row, "question"));
        vo.setScore(intVal(row, "score"));

        if (questionType == 1) {
            vo.setAnswerA(str(row, "answerA"));
            vo.setAnswerB(str(row, "answerB"));
            vo.setAnswerC(str(row, "answerC"));
            vo.setAnswerD(str(row, "answerD"));
        }
        return vo;
    }

    private String findCorrectAnswer(List<Map<String, Object>> mappings, Integer questionId, Integer questionType) {
        // 从 paper_manage 找到对应条目，然后查对应题表获取正确答案
        for (Map<String, Object> m : mappings) {
            if (Objects.equals(m.get("questionId"), questionId) && Objects.equals(m.get("questionType"), questionType)) {
                Map<String, Object> q = null;
                if (questionType == 1) q = paperManageMapper.findMultiById(questionId);
                else if (questionType == 2) q = paperManageMapper.findFillById(questionId);
                else if (questionType == 3) q = paperManageMapper.findJudgeById(questionId);

                if (q != null) {
                    if (questionType == 1) return str(q, "rightAnswer");
                    else return str(q, "answer");
                }
            }
        }
        return null;
    }

    private int findQuestionScore(List<Map<String, Object>> mappings, Integer questionId, Integer questionType) {
        for (Map<String, Object> m : mappings) {
            if (Objects.equals(m.get("questionId"), questionId) && Objects.equals(m.get("questionType"), questionType)) {
                Map<String, Object> q = null;
                if (questionType == 1) q = paperManageMapper.findMultiById(questionId);
                else if (questionType == 2) q = paperManageMapper.findFillById(questionId);
                else if (questionType == 3) q = paperManageMapper.findJudgeById(questionId);
                if (q != null) return intVal(q, "score");
            }
        }
        return 0;
    }

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? "" : String.valueOf(v);
    }

    private int intVal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v != null) {
            try { return Integer.parseInt(String.valueOf(v)); } catch (NumberFormatException ignored) {}
        }
        return 0;
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim();
    }
}
