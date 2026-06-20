package com.exam.service.impl;

import com.exam.entity.ExamManage;
import com.exam.entity.FillQuestion;
import com.exam.entity.JudgeQuestion;
import com.exam.entity.MultiQuestion;
import com.exam.mapper.ExamManageMapper;
import com.exam.mapper.FillQuestionMapper;
import com.exam.mapper.JudgeQuestionMapper;
import com.exam.mapper.MultiQuestionMapper;
import com.exam.mapper.StudentMapper;
import com.exam.service.ImportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.*;

@Service
public class ImportServiceImpl implements ImportService {

    private final StudentMapper studentMapper;
    private final MultiQuestionMapper multiMapper;
    private final FillQuestionMapper fillMapper;
    private final JudgeQuestionMapper judgeMapper;
    private final ExamManageMapper examMapper;

    public ImportServiceImpl(StudentMapper studentMapper,
                             MultiQuestionMapper multiMapper,
                             FillQuestionMapper fillMapper,
                             JudgeQuestionMapper judgeMapper,
                             ExamManageMapper examMapper) {
        this.studentMapper = studentMapper;
        this.multiMapper = multiMapper;
        this.fillMapper = fillMapper;
        this.judgeMapper = judgeMapper;
        this.examMapper = examMapper;
    }

    @Override
    public Map<String, Object> importStudents(byte[] fileBytes) {
        return parseExcel(fileBytes, (row, rowNum, result, rowErrors) -> {
            try {
                String studentName = getCell(row, 0);
                String grade = getCell(row, 1);
                String major = getCell(row, 2);
                String clazz = getCell(row, 3);
                String institute = getCell(row, 4);
                String tel = getCell(row, 5);
                String email = getCell(row, 6);
                String pwd = getCell(row, 7);
                String cardId = getCell(row, 8);
                String sex = getCell(row, 9);

                if (studentName == null || studentName.isEmpty()) {
                    result[1] = (Integer) result[1] + 1;
                    rowErrors.add("第" + rowNum + "行: 姓名为空，已跳过");
                    return;
                }

                com.exam.entity.Student s = new com.exam.entity.Student();
                s.setStudentName(studentName);
                s.setGrade(grade);
                s.setMajor(major);
                s.setClazz(clazz);
                s.setInstitute(institute);
                s.setTel(tel);
                s.setEmail(email);
                s.setPwd(pwd != null ? pwd : "123456");
                s.setCardId(cardId);
                s.setSex(sex != null ? sex : "男");
                s.setRole("2");
                studentMapper.insert(s);
                result[0] = (Integer) result[0] + 1;
            } catch (Exception e) {
                result[1] = (Integer) result[1] + 1;
                rowErrors.add("第" + rowNum + "行插入失败: " + e.getMessage());
            }
        });
    }

    @Override
    public Map<String, Object> importMultiQuestions(byte[] fileBytes) {
        return parseExcel(fileBytes, (row, rowNum, result, rowErrors) -> {
            try {
                String subject = getCell(row, 0);
                String question = getCell(row, 1);
                String a = getCell(row, 2);
                String b = getCell(row, 3);
                String c = getCell(row, 4);
                String d = getCell(row, 5);
                String right = getCell(row, 6);
                String analysis = getCell(row, 7);
                String scoreStr = getCell(row, 8);
                String section = getCell(row, 9);
                String level = getCell(row, 10);

                if (subject == null || question == null) {
                    result[1] = (Integer) result[1] + 1;
                    rowErrors.add("第" + rowNum + "行: 科目或题目为空，已跳过");
                    return;
                }

                MultiQuestion q = new MultiQuestion();
                q.setSubject(subject);
                q.setQuestion(question);
                q.setAnswerA(a);
                q.setAnswerB(b);
                q.setAnswerC(c);
                q.setAnswerD(d);
                q.setRightAnswer(right);
                q.setAnalysis(analysis);
                q.setScore(parseScore(scoreStr, 2));
                q.setSection(section);
                q.setLevel(level);
                multiMapper.insert(q);
                result[0] = (Integer) result[0] + 1;
            } catch (Exception e) {
                result[1] = (Integer) result[1] + 1;
                rowErrors.add("第" + rowNum + "行插入失败: " + e.getMessage());
            }
        });
    }

    @Override
    public Map<String, Object> importFillQuestions(byte[] fileBytes) {
        return parseExcel(fileBytes, (row, rowNum, result, rowErrors) -> {
            try {
                String subject = getCell(row, 0);
                String question = getCell(row, 1);
                String answer = getCell(row, 2);
                String analysis = getCell(row, 3);
                String scoreStr = getCell(row, 4);
                String section = getCell(row, 5);
                String level = getCell(row, 6);

                if (subject == null || question == null) {
                    result[1] = (Integer) result[1] + 1;
                    rowErrors.add("第" + rowNum + "行: 科目或题目为空，已跳过");
                    return;
                }

                FillQuestion q = new FillQuestion();
                q.setSubject(subject);
                q.setQuestion(question);
                q.setAnswer(answer);
                q.setAnalysis(analysis);
                q.setScore(parseScore(scoreStr, 2));
                q.setSection(section);
                q.setLevel(level);
                fillMapper.insert(q);
                result[0] = (Integer) result[0] + 1;
            } catch (Exception e) {
                result[1] = (Integer) result[1] + 1;
                rowErrors.add("第" + rowNum + "行插入失败: " + e.getMessage());
            }
        });
    }

    @Override
    public Map<String, Object> importJudgeQuestions(byte[] fileBytes) {
        return parseExcel(fileBytes, (row, rowNum, result, rowErrors) -> {
            try {
                String subject = getCell(row, 0);
                String question = getCell(row, 1);
                String answer = getCell(row, 2);
                String analysis = getCell(row, 3);
                String scoreStr = getCell(row, 4);
                String section = getCell(row, 5);
                String level = getCell(row, 6);

                if (subject == null || question == null) {
                    result[1] = (Integer) result[1] + 1;
                    rowErrors.add("第" + rowNum + "行: 科目或题目为空，已跳过");
                    return;
                }

                JudgeQuestion q = new JudgeQuestion();
                q.setSubject(subject);
                q.setQuestion(question);
                q.setAnswer(answer);
                q.setAnalysis(analysis);
                q.setScore(parseScore(scoreStr, 2));
                q.setSection(section);
                q.setLevel(level);
                judgeMapper.insert(q);
                result[0] = (Integer) result[0] + 1;
            } catch (Exception e) {
                result[1] = (Integer) result[1] + 1;
                rowErrors.add("第" + rowNum + "行插入失败: " + e.getMessage());
            }
        });
    }

    @Override
    public Map<String, Object> importExams(byte[] fileBytes) {
        return parseExcel(fileBytes, (row, rowNum, result, rowErrors) -> {
            try {
                String desc = getCell(row, 0);
                String source = getCell(row, 1);
                String paperIdStr = getCell(row, 2);
                String examDate = getCell(row, 3);
                String totalTimeStr = getCell(row, 4);
                String grade = getCell(row, 5);
                String term = getCell(row, 6);
                String major = getCell(row, 7);
                String institute = getCell(row, 8);
                String totalScoreStr = getCell(row, 9);
                String type = getCell(row, 10);
                String tips = getCell(row, 11);

                if (desc == null || source == null) {
                    result[1] = (Integer) result[1] + 1;
                    rowErrors.add("第" + rowNum + "行: 描述或科目为空，已跳过");
                    return;
                }

                ExamManage e = new ExamManage();
                e.setDescription(desc);
                e.setSource(source);
                e.setPaperId(paperIdStr != null ? parseInt(paperIdStr) : null);
                e.setExamDate(examDate);
                e.setTotalTime(totalTimeStr != null ? parseInt(totalTimeStr) : 120);
                e.setGrade(grade);
                e.setTerm(term);
                e.setMajor(major);
                e.setInstitute(institute);
                e.setTotalScore(totalScoreStr != null ? parseInt(totalScoreStr) : 100);
                e.setType(type);
                e.setTips(tips);
                examMapper.insert(e);
                result[0] = (Integer) result[0] + 1;
            } catch (Exception ex) {
                result[1] = (Integer) result[1] + 1;
                rowErrors.add("第" + rowNum + "行插入失败: " + ex.getMessage());
            }
        });
    }

    // ---- 通用 Excel 解析 ----
    private Map<String, Object> parseExcel(byte[] bytes, RowHandler handler) {
        Map<String, Object> result = new LinkedHashMap<>();
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 跳过表头
                Row row = sheet.getRow(i);
                if (row == null) continue;
                int[] counter = {0, 0};
                List<String> rowErrors = new ArrayList<>();
                handler.handle(row, i + 1, counter, rowErrors);
                success += counter[0];
                fail += counter[1];
                if (!rowErrors.isEmpty()) errors.addAll(rowErrors);
            }
        } catch (Exception e) {
            result.put("error", "文件解析失败: " + e.getMessage());
            return result;
        }
        result.put("success", success);
        result.put("fail", fail);
        result.put("total", success + fail);
        result.put("errors", errors);
        return result;
    }

    private String getCell(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                String val = cell.getStringCellValue();
                return val != null ? val.trim() : null;
            case NUMERIC:
                // 数值单元格：避免 setCellType(STRING) 产生 "2.0" 等非预期格式
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d) && !Double.isInfinite(d)) {
                    return String.valueOf((long) d);
                }
                return String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue(); } catch (Exception e) { return String.valueOf(cell.getNumericCellValue()); }
            default:
                return null;
        }
    }

    private int parseInt(String s) {
        if (s == null) return 0;
        try {
            // Apache POI setCellType(STRING) 可能将整数 2 转换为 "2.0"，直接 Integer.parseInt 会抛异常
            return (int) Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /** 解析分值，<=0 或解析失败时使用默认值 */
    private int parseScore(String s, int defaultScore) {
        int score = parseInt(s);
        return score > 0 ? score : defaultScore;
    }

    @FunctionalInterface
    private interface RowHandler {
        void handle(Row row, int rowNum, int[] result, List<String> rowErrors);
    }
}
