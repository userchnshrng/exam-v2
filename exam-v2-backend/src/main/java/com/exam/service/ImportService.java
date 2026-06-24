package com.exam.service;

import java.util.Map;

public interface ImportService {

    /** 导入学生 */
    Map<String, Object> importStudents(byte[] fileBytes);

    /** 导入选择题 */
    Map<String, Object> importMultiQuestions(byte[] fileBytes);

    /** 导入填空题 */
    Map<String, Object> importFillQuestions(byte[] fileBytes);

    /** 导入判断题 */
    Map<String, Object> importJudgeQuestions(byte[] fileBytes);

    /** 导入考试 */
    Map<String, Object> importExams(byte[] fileBytes);

    /** 导入教师 */
    Map<String, Object> importTeachers(byte[] fileBytes);
}
