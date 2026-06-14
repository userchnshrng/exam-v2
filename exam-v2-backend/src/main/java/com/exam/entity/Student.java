package com.exam.entity;

/**
 * 学生实体 — 对应 student 表
 */
public class Student {

    private Integer studentId;
    private String studentName;
    private String grade;
    private String major;
    private String clazz;
    private String institute;
    private String tel;
    private String email;
    private String pwd;
    private String cardId;
    private String sex;
    private String role;

    public Student() {
    }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getClazz() { return clazz; }
    public void setClazz(String clazz) { this.clazz = clazz; }

    public String getInstitute() { return institute; }
    public void setInstitute(String institute) { this.institute = institute; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
