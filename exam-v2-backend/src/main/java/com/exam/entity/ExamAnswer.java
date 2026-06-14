package com.exam.entity;

/**
 * 答题记录 — 对应 exam_answer 表
 */
public class ExamAnswer {

    private Integer answerId;
    private Integer examCode;
    private Integer studentId;
    private Integer questionType;
    private Integer questionId;
    private String studentAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private Integer score;

    public ExamAnswer() {
    }

    public Integer getAnswerId() { return answerId; }
    public void setAnswerId(Integer answerId) { this.answerId = answerId; }
    public Integer getExamCode() { return examCode; }
    public void setExamCode(Integer examCode) { this.examCode = examCode; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Integer getQuestionType() { return questionType; }
    public void setQuestionType(Integer questionType) { this.questionType = questionType; }
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getStudentAnswer() { return studentAnswer; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
