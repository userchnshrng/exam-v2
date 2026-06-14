package com.exam.service.impl;

import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final ScoreMapper scoreMapper;
    private final ExamManageMapper examMapper;

    public ExportServiceImpl(StudentMapper studentMapper, TeacherMapper teacherMapper,
                             ScoreMapper scoreMapper, ExamManageMapper examMapper) {
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.scoreMapper = scoreMapper;
        this.examMapper = examMapper;
    }

    @Override
    public void exportStudents(HttpServletResponse response) throws IOException {
        List<Student> list = studentMapper.list("", 0, 10000);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("学生信息");
        Row header = sheet.createRow(0);
        String[] heads = {"学号", "姓名", "年级", "专业", "班级", "学院", "电话", "邮箱", "性别"};
        for (int i = 0; i < heads.length; i++) header.createCell(i).setCellValue(heads[i]);
        int r = 1;
        for (Student s : list) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(s.getStudentId() != null ? String.valueOf(s.getStudentId()) : "");
            row.createCell(1).setCellValue(nn(s.getStudentName()));
            row.createCell(2).setCellValue(nn(s.getGrade()));
            row.createCell(3).setCellValue(nn(s.getMajor()));
            row.createCell(4).setCellValue(nn(s.getClazz()));
            row.createCell(5).setCellValue(nn(s.getInstitute()));
            row.createCell(6).setCellValue(nn(s.getTel()));
            row.createCell(7).setCellValue(nn(s.getEmail()));
            row.createCell(8).setCellValue(nn(s.getSex()));
        }
        download(response, wb, "学生信息.xlsx");
    }

    @Override
    public void exportTeachers(HttpServletResponse response) throws IOException {
        List<Teacher> list = teacherMapper.list("", 0, 10000);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("教师信息");
        Row header = sheet.createRow(0);
        String[] heads = {"工号", "姓名", "学院", "性别", "电话", "邮箱", "职称"};
        for (int i = 0; i < heads.length; i++) header.createCell(i).setCellValue(heads[i]);
        int r = 1;
        for (Teacher t : list) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(t.getTeacherId() != null ? String.valueOf(t.getTeacherId()) : "");
            row.createCell(1).setCellValue(nn(t.getTeacherName()));
            row.createCell(2).setCellValue(nn(t.getInstitute()));
            row.createCell(3).setCellValue(nn(t.getSex()));
            row.createCell(4).setCellValue(nn(t.getTel()));
            row.createCell(5).setCellValue(nn(t.getEmail()));
            row.createCell(6).setCellValue(nn(t.getType()));
        }
        download(response, wb, "教师信息.xlsx");
    }

    @Override
    public void exportScores(HttpServletResponse response, Integer examCode) throws IOException {
        List<Score> list = scoreMapper.list(null, 0, 10000);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("成绩数据");
        Row header = sheet.createRow(0);
        String[] heads = {"成绩ID", "考试编号", "学号", "科目", "得分", "答题日期"};
        for (int i = 0; i < heads.length; i++) header.createCell(i).setCellValue(heads[i]);
        int r = 1;
        for (Score s : list) {
            if (examCode != null && !examCode.equals(s.getExamCode())) continue;
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(s.getScoreId() != null ? String.valueOf(s.getScoreId()) : "");
            row.createCell(1).setCellValue(s.getExamCode() != null ? String.valueOf(s.getExamCode()) : "");
            row.createCell(2).setCellValue(s.getStudentId() != null ? String.valueOf(s.getStudentId()) : "");
            row.createCell(3).setCellValue(nn(s.getSubject()));
            row.createCell(4).setCellValue(s.getEtScore() != null ? String.valueOf(s.getEtScore()) : "");
            row.createCell(5).setCellValue(nn(s.getAnswerDate()));
        }
        download(response, wb, "成绩数据.xlsx");
    }

    @Override
    public void exportExams(HttpServletResponse response) throws IOException {
        List<ExamManage> list = examMapper.list("", 0, 10000);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("考试数据");
        Row header = sheet.createRow(0);
        String[] heads = {"编号", "描述", "科目", "试卷ID", "日期", "时长", "年级", "学期", "专业", "学院", "总分", "类型"};
        for (int i = 0; i < heads.length; i++) header.createCell(i).setCellValue(heads[i]);
        int r = 1;
        for (ExamManage e : list) {
            Row row = sheet.createRow(r++);
            int c = 0;
            row.createCell(c++).setCellValue(e.getExamCode() != null ? String.valueOf(e.getExamCode()) : "");
            row.createCell(c++).setCellValue(nn(e.getDescription()));
            row.createCell(c++).setCellValue(nn(e.getSource()));
            row.createCell(c++).setCellValue(e.getPaperId() != null ? String.valueOf(e.getPaperId()) : "");
            row.createCell(c++).setCellValue(nn(e.getExamDate()));
            row.createCell(c++).setCellValue(e.getTotalTime() != null ? String.valueOf(e.getTotalTime()) : "");
            row.createCell(c++).setCellValue(nn(e.getGrade()));
            row.createCell(c++).setCellValue(nn(e.getTerm()));
            row.createCell(c++).setCellValue(nn(e.getMajor()));
            row.createCell(c++).setCellValue(nn(e.getInstitute()));
            row.createCell(c++).setCellValue(e.getTotalScore() != null ? String.valueOf(e.getTotalScore()) : "");
            row.createCell(c++).setCellValue(nn(e.getType()));
        }
        download(response, wb, "考试数据.xlsx");
    }

    private String nn(String s) { return s != null ? s : ""; }

    private void download(HttpServletResponse response, Workbook wb, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        wb.write(response.getOutputStream());
        wb.close();
    }
}
