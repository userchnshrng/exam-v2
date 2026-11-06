package com.exam.service.impl;

import com.exam.entity.Score;
import com.exam.mapper.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreExportTest {
    @Test void exportsBlankAttemptAsNumericZeroAndKeepsEachAttemptIndependent() throws Exception {
        ScoreMapper scores = mock(ScoreMapper.class);
        when(scores.listForExport(200)).thenReturn(List.of(score(12, 0), score(11, 80)));
        ExportServiceImpl service = new ExportServiceImpl(mock(StudentMapper.class), mock(TeacherMapper.class), scores, mock(ExamManageMapper.class));
        MockHttpServletResponse response = new MockHttpServletResponse();
        service.exportScores(response, 200);
        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            var sheet = workbook.getSheetAt(0);
            assertEquals(2, sheet.getLastRowNum());
            assertEquals("12", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals(CellType.NUMERIC, sheet.getRow(1).getCell(4).getCellType());
            assertEquals(0, sheet.getRow(1).getCell(4).getNumericCellValue());
            assertEquals("11", sheet.getRow(2).getCell(0).getStringCellValue());
            assertEquals(80, sheet.getRow(2).getCell(4).getNumericCellValue());
        }
        verify(scores).listForExport(200);
        verify(scores, never()).list(any(), anyInt(), anyInt());
    }

    @Test void allExamExportUsesUnboundedExportQuery() throws Exception {
        ScoreMapper scores = mock(ScoreMapper.class);
        when(scores.listForExport(null)).thenReturn(List.of(score(12, 0)));
        ExportServiceImpl service = new ExportServiceImpl(mock(StudentMapper.class), mock(TeacherMapper.class), scores, mock(ExamManageMapper.class));
        service.exportScores(new MockHttpServletResponse(), null);
        verify(scores).listForExport(null);
        verifyNoMoreInteractions(scores);
    }

    private Score score(int id, int points) {
        Score score = new Score(); score.setScoreId(id); score.setExamCode(200); score.setStudentId(100);
        score.setSubject("测试科目"); score.setEtScore(points); score.setAnswerDate("2026-11-06"); return score;
    }
}
