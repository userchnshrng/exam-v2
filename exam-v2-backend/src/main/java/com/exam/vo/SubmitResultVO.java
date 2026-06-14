package com.exam.vo;

import java.util.List;

/**
 * 提交试卷结果
 */
public class SubmitResultVO {

    private int totalScore;             // 总分
    private int correctCount;           // 正确题数
    private int totalCount;             // 总题数
    private List<AnswerResult> details; // 每题详情

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public List<AnswerResult> getDetails() { return details; }
    public void setDetails(List<AnswerResult> details) { this.details = details; }

    public static class AnswerResult {
        private Integer questionId;
        private Integer questionType;
        private String studentAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Integer score;

        public Integer getQuestionId() { return questionId; }
        public void setQuestionId(Integer questionId) { this.questionId = questionId; }
        public Integer getQuestionType() { return questionType; }
        public void setQuestionType(Integer questionType) { this.questionType = questionType; }
        public String getStudentAnswer() { return studentAnswer; }
        public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        public Boolean getIsCorrect() { return isCorrect; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }
}
