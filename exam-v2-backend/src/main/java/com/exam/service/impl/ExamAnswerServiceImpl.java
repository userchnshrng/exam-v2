package com.exam.service.impl;

import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.ExamAnswerService;
import com.exam.vo.ExamAnswerVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamAnswerServiceImpl implements ExamAnswerService {

    private final ExamAnswerMapper examAnswerMapper;
    private final MultiQuestionMapper multiQuestionMapper;
    private final FillQuestionMapper fillQuestionMapper;
    private final JudgeQuestionMapper judgeQuestionMapper;

    public ExamAnswerServiceImpl(ExamAnswerMapper examAnswerMapper,
                                 MultiQuestionMapper multiQuestionMapper,
                                 FillQuestionMapper fillQuestionMapper,
                                 JudgeQuestionMapper judgeQuestionMapper) {
        this.examAnswerMapper = examAnswerMapper;
        this.multiQuestionMapper = multiQuestionMapper;
        this.fillQuestionMapper = fillQuestionMapper;
        this.judgeQuestionMapper = judgeQuestionMapper;
    }

    @Override
    public List<ExamAnswerVO> getByExamAndStudent(Integer examCode, Integer studentId) {
        List<ExamAnswer> answers = examAnswerMapper.findByExamAndStudent(examCode, studentId);
        List<ExamAnswerVO> result = new ArrayList<>();

        for (ExamAnswer a : answers) {
            ExamAnswerVO vo = new ExamAnswerVO();
            vo.setAnswerId(a.getAnswerId());
            vo.setExamCode(a.getExamCode());
            vo.setStudentId(a.getStudentId());
            vo.setQuestionType(a.getQuestionType());
            vo.setQuestionId(a.getQuestionId());
            vo.setStudentAnswer(a.getStudentAnswer());
            vo.setCorrectAnswer(a.getCorrectAnswer());
            vo.setIsCorrect(a.getIsCorrect());
            vo.setScore(a.getScore());

            // 根据题型查题库，填入题干内容
            if (a.getQuestionType() != null) {
                switch (a.getQuestionType()) {
                    case 1 -> { // 选择题
                        MultiQuestion mq = multiQuestionMapper.findById(a.getQuestionId());
                        if (mq != null) {
                            vo.setQuestionContent(mq.getQuestion());
                            vo.setOptionA("A. " + (mq.getAnswerA() != null ? mq.getAnswerA() : ""));
                            vo.setOptionB("B. " + (mq.getAnswerB() != null ? mq.getAnswerB() : ""));
                            vo.setOptionC("C. " + (mq.getAnswerC() != null ? mq.getAnswerC() : ""));
                            vo.setOptionD("D. " + (mq.getAnswerD() != null ? mq.getAnswerD() : ""));
                        }
                    }
                    case 2 -> { // 填空题
                        FillQuestion fq = fillQuestionMapper.findById(a.getQuestionId());
                        if (fq != null) {
                            vo.setQuestionContent(fq.getQuestion());
                        }
                    }
                    case 3 -> { // 判断题
                        JudgeQuestion jq = judgeQuestionMapper.findById(a.getQuestionId());
                        if (jq != null) {
                            vo.setQuestionContent(jq.getQuestion());
                        }
                    }
                }
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    public int deleteByExamAndStudent(Integer examCode, Integer studentId) {
        return examAnswerMapper.deleteByExamAndStudent(examCode, studentId);
    }

    @Override
    public int updateAnswer(ExamAnswer answer) {
        return examAnswerMapper.update(answer);
    }
}
