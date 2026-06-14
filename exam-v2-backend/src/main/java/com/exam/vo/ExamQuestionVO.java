package com.exam.vo;

/**
 * 统一考题 VO — 返回给学生时不含正确答案
 */
public class ExamQuestionVO {

    private Integer questionId;
    private Integer questionType;  // 1=选择 2=填空 3=判断
    private String subject;
    private String question;
    private Integer score;
    // 选择题特有
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;

    public ExamQuestionVO() {
    }

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public Integer getQuestionType() { return questionType; }
    public void setQuestionType(Integer questionType) { this.questionType = questionType; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getAnswerA() { return answerA; }
    public void setAnswerA(String answerA) { this.answerA = answerA; }
    public String getAnswerB() { return answerB; }
    public void setAnswerB(String answerB) { this.answerB = answerB; }
    public String getAnswerC() { return answerC; }
    public void setAnswerC(String answerC) { this.answerC = answerC; }
    public String getAnswerD() { return answerD; }
    public void setAnswerD(String answerD) { this.answerD = answerD; }
}
