package com.exam.service.impl;

import com.exam.entity.JudgeQuestion;
import com.exam.mapper.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ImportServiceImplTest {

    @Test
    void importsHeaderlessJudgeWorkbookAndNormalizesChineseFields() throws Exception {
        JudgeQuestionMapper judgeMapper = mock(JudgeQuestionMapper.class);
        ImportServiceImpl service = createService(judgeMapper);

        Map<String, Object> result = service.importJudgeQuestions(workbookBytes(new XSSFWorkbook(), false));

        assertEquals(1, result.get("success"));
        assertEquals(0, result.get("fail"));
        ArgumentCaptor<JudgeQuestion> captor = ArgumentCaptor.forClass(JudgeQuestion.class);
        verify(judgeMapper).insert(captor.capture());
        assertEquals("T", captor.getValue().getAnswer());
        assertEquals("2", captor.getValue().getLevel());
    }

    @Test
    void supportsLegacyXlsAndSkipsHeader() throws Exception {
        JudgeQuestionMapper judgeMapper = mock(JudgeQuestionMapper.class);
        ImportServiceImpl service = createService(judgeMapper);

        Map<String, Object> result = service.importJudgeQuestions(workbookBytes(new HSSFWorkbook(), true));

        assertEquals(1, result.get("success"));
        verify(judgeMapper, times(1)).insert(any(JudgeQuestion.class));
    }

    private ImportServiceImpl createService(JudgeQuestionMapper judgeMapper) {
        return new ImportServiceImpl(mock(StudentMapper.class), mock(TeacherMapper.class),
                mock(MultiQuestionMapper.class), mock(FillQuestionMapper.class), judgeMapper,
                mock(ExamManageMapper.class));
    }

    private byte[] workbookBytes(Workbook workbook, boolean withHeader) throws Exception {
        var sheet = workbook.createSheet("判断题");
        int rowIndex = 0;
        if (withHeader) {
            Row header = sheet.createRow(rowIndex++);
            String[] values = {"科目", "题目", "答案", "解析", "分值", "章节", "难度"};
            for (int i = 0; i < values.length; i++) header.createCell(i).setCellValue(values[i]);
        }
        Row row = sheet.createRow(rowIndex);
        String[] values = {"单片机原理与应用", "MCS-51是8位微控制器", "正确", "内部总线宽度为8位", "2", "第一章", "简单"};
        for (int i = 0; i < values.length; i++) row.createCell(i).setCellValue(values[i]);
        Row summary = sheet.createRow(rowIndex + 1);
        summary.createCell(0).setCellValue("试卷与题库总计：共 1 道判断题");
        summary.createCell(4).setCellValue(2);
        try (workbook; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.write(output);
            return output.toByteArray();
        }
    }
}
