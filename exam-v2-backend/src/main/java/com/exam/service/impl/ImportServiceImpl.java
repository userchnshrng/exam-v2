package com.exam.service.impl;

import com.exam.entity.ExamManage;
import com.exam.entity.FillQuestion;
import com.exam.entity.JudgeQuestion;
import com.exam.entity.MultiQuestion;
import com.exam.entity.Teacher;
import com.exam.mapper.ExamManageMapper;
import com.exam.mapper.FillQuestionMapper;
import com.exam.mapper.JudgeQuestionMapper;
import com.exam.mapper.MultiQuestionMapper;
import com.exam.mapper.StudentMapper;
import com.exam.mapper.TeacherMapper;
import com.exam.service.ImportService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.*;

@Service
public class ImportServiceImpl implements ImportService {

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final MultiQuestionMapper multiMapper;
    private final FillQuestionMapper fillMapper;
    private final JudgeQuestionMapper judgeMapper;
    private final ExamManageMapper examMapper;

    public ImportServiceImpl(StudentMapper studentMapper,
                             TeacherMapper teacherMapper,
                             MultiQuestionMapper multiMapper,
                             FillQuestionMapper fillMapper,
                             JudgeQuestionMapper judgeMapper,
                             ExamManageMapper examMapper) {
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
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

                answer = normalizeJudgeAnswer(answer);
                if (answer == null) {
                    result[1] = (Integer) result[1] + 1;
                    rowErrors.add("第" + rowNum + "行: 判断题答案必须为 T/F、正确/错误、对/错或 1/0");
                    return;
                }

                JudgeQuestion q = new JudgeQuestion();
                q.setSubject(subject);
                q.setQuestion(question);
                q.setAnswer(answer);
                q.setAnalysis(analysis);
                q.setScore(parseScore(scoreStr, 2));
                q.setSection(section);
                q.setLevel(normalizeQuestionLevel(level));
                judgeMapper.insert(q);
                result[0] = (Integer) result[0] + 1;
            } catch (Exception e) {
                result[1] = (Integer) result[1] + 1;
                rowErrors.add("第" + rowNum + "行插入失败: " + conciseDatabaseError(e));
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

    @Override
    public Map<String, Object> importTeachers(byte[] fileBytes) {
        return parseExcel(fileBytes, (row, rowNum, result, rowErrors) -> {
            try {
                String teacherName = getCell(row, 0);
                String institute = getCell(row, 1);
                String sex = getCell(row, 2);
                String tel = getCell(row, 3);
                String email = getCell(row, 4);
                String pwd = getCell(row, 5);
                String cardId = getCell(row, 6);
                String type = getCell(row, 7);

                if (teacherName == null || teacherName.isEmpty()) {
                    result[1] = (Integer) result[1] + 1;
                    rowErrors.add("第" + rowNum + "行: 姓名为空，已跳过");
                    return;
                }

                Teacher t = new Teacher();
                t.setTeacherName(teacherName);
                t.setInstitute(institute);
                t.setSex(sex != null ? sex : "男");
                t.setTel(tel);
                t.setEmail(email);
                t.setPwd(pwd != null ? pwd : "123456");
                t.setCardId(cardId);
                t.setType(type);
                t.setRole("1");
                teacherMapper.insert(t);
                result[0] = (Integer) result[0] + 1;
            } catch (Exception e) {
                result[1] = (Integer) result[1] + 1;
                rowErrors.add("第" + rowNum + "行插入失败: " + e.getMessage());
            }
        });
    }

    // ---- 通用 Excel 解析 ----
    private Map<String, Object> parseExcel(byte[] bytes, RowHandler handler) {
        Map<String, Object> result = new LinkedHashMap<>();
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            Sheet sheet = wb.getSheetAt(0);
            int firstRow = sheet.getFirstRowNum();
            if (isHeaderRow(sheet.getRow(firstRow))) firstRow++;
            for (int i = firstRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isBlankRow(row) || isSummaryRow(row)) continue;
                int[] counter = {0, 0};
                List<String> rowErrors = new ArrayList<>();
                handler.handle(row, i + 1, counter, rowErrors);
                success += counter[0];
                fail += counter[1];
                if (!rowErrors.isEmpty()) errors.addAll(rowErrors);
            }
        } catch (Exception e) {
            result.put("error", "文件解析失败: " + e.getMessage());
            result.put("success", 0);
            result.put("fail", 1);
            result.put("total", 1);
            result.put("errors", List.of("文件解析失败: " + e.getMessage()));
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

    private boolean isHeaderRow(Row row) {
        if (row == null) return false;
        Set<String> headers = Set.of("姓名", "科目", "题目", "问题", "描述", "考试名称", "教师姓名", "学生姓名");
        for (int i = 0; i < Math.min(row.getLastCellNum(), 3); i++) {
            String value = getCell(row, i);
            if (value != null && headers.contains(value.replace(" ", ""))) return true;
        }
        return false;
    }

    private boolean isBlankRow(Row row) {
        if (row.getFirstCellNum() < 0) return true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            String value = getCell(row, i);
            if (value != null && !value.isBlank()) return false;
        }
        return true;
    }

    private boolean isSummaryRow(Row row) {
        String firstCell = getCell(row, 0);
        String secondCell = getCell(row, 1);
        return (secondCell == null || secondCell.isBlank()) && firstCell != null
                && (firstCell.contains("总计") || firstCell.contains("合计"));
    }

    private String normalizeJudgeAnswer(String answer) {
        if (answer == null) return null;
        return switch (answer.trim().toUpperCase(Locale.ROOT)) {
            case "T", "TRUE", "1", "正确", "对", "是" -> "T";
            case "F", "FALSE", "0", "错误", "错", "否" -> "F";
            default -> null;
        };
    }

    private String normalizeQuestionLevel(String level) {
        if (level == null || level.isBlank()) return null;
        String normalized = level.trim();
        return switch (normalized) {
            case "1", "非常简单", "很简单" -> "1";
            case "2", "简单", "较简单", "容易" -> "2";
            case "3", "中等", "一般" -> "3";
            case "4", "较难" -> "4";
            case "5", "困难", "很难", "非常困难" -> "5";
            default -> throw new IllegalArgumentException("难度必须为1-5或简单/中等/较难/困难");
        };
    }

    private String conciseDatabaseError(Exception error) {
        String message = error.getMessage();
        if (message == null || message.isBlank()) return "数据库写入失败";
        if (message.contains("Data too long for column")) return "字段内容超过数据库长度限制";
        if (message.contains("Duplicate entry")) return "数据重复";
        if (message.contains("cannot be null")) return "存在必填字段为空";
        int causeIndex = message.lastIndexOf("### Cause:");
        if (causeIndex >= 0) message = message.substring(causeIndex + "### Cause:".length()).trim();
        return message.length() > 160 ? message.substring(0, 160) + "…" : message;
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
