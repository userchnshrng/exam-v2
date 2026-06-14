package com.exam.entity;

/**
 * 选择题 — 对应 multi_question 表
 */
public class MultiQuestion {

    private Integer questionId;
    private String subject;
    private String question;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String rightAnswer;
    private String analysis;
    private Integer score;
    private String section;
    private String level;

    public MultiQuestion() {
    }

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswerA() { return answerA; }
    public void setAnswerA(String answerA) { this.answerA = answerA; }
    public String getAnswerB() { return answerB; }
    public void setAnswerB(String answerB) { this.answerB = answerB; }
    public String getAnswerC() { return answerC; }
    public void setAnswerC(String answerC) { this.answerC = answerC; }
    public String getAnswerD() { return answerD; }
    public void setAnswerD(String answerD) { this.answerD = answerD; }
    public String getRightAnswer() { return rightAnswer; }
    public void setRightAnswer(String rightAnswer) { this.rightAnswer = rightAnswer; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
