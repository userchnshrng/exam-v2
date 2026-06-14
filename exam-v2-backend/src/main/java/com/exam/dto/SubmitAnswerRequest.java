package com.exam.dto;

import java.util.List;

/**
 * 提交试卷请求
 */
public class SubmitAnswerRequest {

    private Integer examCode;
    private Integer studentId;
    private List<AnswerItem> answers;

    public Integer getExamCode() { return examCode; }
    public void setExamCode(Integer examCode) { this.examCode = examCode; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public List<AnswerItem> getAnswers() { return answers; }
    public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }

    public static class AnswerItem {
        private Integer questionType;
        private Integer questionId;
        private String answer;

        public Integer getQuestionType() { return questionType; }
        public void setQuestionType(Integer questionType) { this.questionType = questionType; }
        public Integer getQuestionId() { return questionId; }
        public void setQuestionId(Integer questionId) { this.questionId = questionId; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}
