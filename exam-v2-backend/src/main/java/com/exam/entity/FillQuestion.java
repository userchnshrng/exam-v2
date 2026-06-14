package com.exam.entity;

/**
 * 填空题 — 对应 fill_question 表
 */
public class FillQuestion {

    private Integer questionId;
    private String subject;
    private String question;
    private String answer;
    private String analysis;
    private Integer score;
    private String level;
    private String section;

    public FillQuestion() {
    }

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}
