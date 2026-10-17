package com.exam.service.impl;

import com.exam.entity.ExamManage;
import com.exam.mapper.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaperServiceImplTest {
    private final PaperManageMapper paper = mock(PaperManageMapper.class);
    private final ExamManageMapper exams = mock(ExamManageMapper.class);
    private final PaperServiceImpl service = new PaperServiceImpl(paper, exams,
            mock(ExamAnswerMapper.class), mock(ScoreMapper.class));

    private void setup(int total, int[] multi, int[] fill, int[] judge) {
        ExamManage exam = new ExamManage();
        exam.setPaperId(1); exam.setSource("计算机网络"); exam.setTotalScore(total);
        when(exams.findById(1)).thenReturn(exam);
        when(paper.listByPaperId(1)).thenReturn(List.of());
        when(paper.listMultiBySubject("计算机网络")).thenReturn(questions(multi));
        when(paper.listFillBySubject("计算机网络")).thenReturn(questions(fill));
        when(paper.listJudgeBySubject("计算机网络")).thenReturn(questions(judge));
    }

    private List<Map<String, Object>> questions(int[] scores) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            result.add(new HashMap<>(Map.of("questionId", i + 1, "score", scores[i], "subject", "计算机网络")));
        }
        return result;
    }

    @Test void balancesAllThreeTypesInsteadOfFillingWithFirstType() {
        setup(12, new int[]{2,2,2,2,2,2}, new int[]{2,2,2,2}, new int[]{2,2,2,2});
        assertEquals(6, service.autoCompose(1, 1));
        ArgumentCaptor<Integer> types = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> ids = ArgumentCaptor.forClass(Integer.class);
        verify(paper, times(6)).insertMapping(eq(1), types.capture(), ids.capture());
        for (int type = 1; type <= 3; type++) assertEquals(2, Collections.frequency(types.getAllValues(), type));
        Set<String> unique = new HashSet<>();
        for (int i = 0; i < 6; i++) assertTrue(unique.add(types.getAllValues().get(i) + "_" + ids.getAllValues().get(i)));
    }

    @Test void adjustsAllocationWhenQuestionScoresDiffer() {
        setup(10, new int[]{6,2}, new int[]{3}, new int[]{1});
        assertEquals(3, service.autoCompose(1, 1));
        verify(paper).insertMapping(1, 1, 1);
        verify(paper).insertMapping(1, 2, 1);
        verify(paper).insertMapping(1, 3, 1);
    }

    @Test void failsWithoutWritingWhenCoverageCannotFit() {
        setup(4, new int[]{2,2}, new int[]{2}, new int[]{2});
        assertThrows(RuntimeException.class, () -> service.autoCompose(1, 1));
        verify(paper, never()).insertMapping(anyInt(), anyInt(), anyInt());
    }

    @Test void preservesExistingSelectionAndCompletesMissingTypes() {
        setup(6, new int[]{2}, new int[]{2}, new int[]{2});
        when(paper.listByPaperId(1)).thenReturn(List.of(Map.of("questionType", 1, "questionId", 1)));
        when(paper.findMultiById(1)).thenReturn(questions(new int[]{2}).get(0));
        assertEquals(2, service.autoCompose(1, 1));
        verify(paper, never()).insertMapping(1, 1, 1);
        verify(paper).insertMapping(1, 2, 1);
        verify(paper).insertMapping(1, 3, 1);
    }

    @Test void rejectsFullPaperThatStillLacksAvailableTypes() {
        setup(2, new int[]{2}, new int[]{2}, new int[]{2});
        when(paper.listByPaperId(1)).thenReturn(List.of(Map.of("questionType", 1, "questionId", 1)));
        when(paper.findMultiById(1)).thenReturn(questions(new int[]{2}).get(0));
        assertThrows(RuntimeException.class, () -> service.autoCompose(1, 1));
        verify(paper, never()).insertMapping(anyInt(), anyInt(), anyInt());
    }

    @Test void coversBothAvailableTypesWhenThirdTypeHasNoQuestions() {
        setup(4, new int[]{2,2}, new int[]{2}, new int[]{});
        assertEquals(2, service.autoCompose(1, 1));
        verify(paper).insertMapping(1, 1, 1);
        verify(paper).insertMapping(1, 2, 1);
    }
}
