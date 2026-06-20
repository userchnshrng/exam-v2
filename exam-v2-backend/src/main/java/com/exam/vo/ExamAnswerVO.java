package com.exam.vo;

/**
 * 答题详情 VO — 包含题目题干，用于考试详情展示
 */
public class ExamAnswerVO {

    // ---- ExamAnswer 原有字段 ----
    private Integer answerId;
    private Integer examCode;
    private Integer studentId;
    private Integer questionType;   // 1=选择题 2=填空题 3=判断题
    private Integer questionId;
    private String studentAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private Integer score;

    // ---- 题目内容（从题库联查） ----
    private String questionContent;  // 题干
    private String optionA;          // 选择题选项A
    private String optionB;          // 选择题选项B
    private String optionC;          // 选择题选项C
    private String optionD;          // 选择题选项D

    public ExamAnswerVO() {}

    // ---- getters / setters ----
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

    public String getQuestionContent() { return questionContent; }
    public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
}
