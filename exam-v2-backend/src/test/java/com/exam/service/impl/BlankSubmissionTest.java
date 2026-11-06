package com.exam.service.impl;

import com.exam.dto.SubmitAnswerRequest;
import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.vo.SubmitResultVO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlankSubmissionTest {
    private final PaperManageMapper paper = mock(PaperManageMapper.class);
    private final ExamManageMapper exams = mock(ExamManageMapper.class);
    private final ExamAnswerMapper answers = mock(ExamAnswerMapper.class);
    private final ScoreMapper scores = mock(ScoreMapper.class);
    private final PaperServiceImpl service = new PaperServiceImpl(paper, exams, answers, scores);

    private void setup() {
        ExamManage exam = new ExamManage();
        exam.setExamCode(200); exam.setPaperId(1); exam.setSource("测试科目");
        when(exams.findById(200)).thenReturn(exam);
        when(paper.listByPaperId(1)).thenReturn(List.of(
                Map.of("questionType", 1, "questionId", 1),
                Map.of("questionType", 2, "questionId", 1),
                Map.of("questionType", 3, "questionId", 1)));
        when(paper.findMultiById(1)).thenReturn(Map.of("questionId", 1, "question", "选择", "rightAnswer", "A", "score", 2));
        when(paper.findFillById(1)).thenReturn(Map.of("questionId", 1, "question", "填空", "answer", "", "score", 2));
        when(paper.findJudgeById(1)).thenReturn(Map.of("questionId", 1, "question", "判断", "answer", "T", "score", 2));
        AtomicInteger nextId = new AtomicInteger(1000);
        doAnswer(call -> { ((Score) call.getArgument(0)).setScoreId(nextId.incrementAndGet()); return 1; })
                .when(scores).insert(any(Score.class));
    }

    @Test void blankAnswersNeverEarnPointsEvenIfCorrectAnswerIsEmpty() {
        setup();
        SubmitResultVO result = service.submitAnswers(request(Arrays.asList(item(1, ""), item(2, "  \t"), item(3, null))));
        assertEquals(0, result.getTotalScore());
        assertEquals(0, result.getCorrectCount());
        assertEquals(3, result.getTotalCount());
        assertNotNull(result.getScoreId());
        ArgumentCaptor<ExamAnswer> saved = ArgumentCaptor.forClass(ExamAnswer.class);
        verify(answers, times(3)).insert(saved.capture());
        for (ExamAnswer answer : saved.getAllValues()) {
            assertEquals("", answer.getStudentAnswer()); assertFalse(answer.getIsCorrect());
            assertEquals(0, answer.getScore()); assertEquals(result.getScoreId(), answer.getScoreId());
        }
        verify(scores).updateEtScore(result.getScoreId(), 0);
    }

    @Test void omittedAnswersStillCreateOneUnansweredRecordForEveryPaperQuestion() {
        setup();
        SubmitResultVO result = service.submitAnswers(request(List.of()));
        assertEquals(3, result.getDetails().size());
        assertTrue(result.getDetails().stream().allMatch(a -> a.getStudentAnswer().isEmpty() && !a.getIsCorrect() && a.getScore() == 0));
    }

    @Test void repeatBlankSubmissionIsSeparatedFromPreviousCorrectAnswers() {
        setup();
        SubmitResultVO first = service.submitAnswers(request(List.of(item(1, "A"), item(3, "T"))));
        SubmitResultVO blank = service.submitAnswers(request(null));
        assertEquals(4, first.getTotalScore());
        assertEquals(0, blank.getTotalScore());
        assertNotEquals(first.getScoreId(), blank.getScoreId());
        ArgumentCaptor<ExamAnswer> saved = ArgumentCaptor.forClass(ExamAnswer.class);
        verify(answers, times(6)).insert(saved.capture());
        assertTrue(saved.getAllValues().subList(3, 6).stream().allMatch(a -> a.getScoreId().equals(blank.getScoreId()) && a.getStudentAnswer().isEmpty()));
    }

    @Test void duplicateAndForeignQuestionPayloadsAreRejectedBeforeWriting() {
        setup();
        assertThrows(RuntimeException.class, () -> service.submitAnswers(request(List.of(item(1, "A"), item(1, "A")))));
        assertThrows(RuntimeException.class, () -> service.submitAnswers(request(List.of(item(4, "A")))));
        verifyNoInteractions(scores, answers);
    }

    private SubmitAnswerRequest request(List<SubmitAnswerRequest.AnswerItem> items) {
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setExamCode(200); request.setStudentId(100); request.setAnswers(items); return request;
    }

    private SubmitAnswerRequest.AnswerItem item(int type, String text) {
        SubmitAnswerRequest.AnswerItem item = new SubmitAnswerRequest.AnswerItem();
        item.setQuestionType(type); item.setQuestionId(1); item.setAnswer(text); return item;
    }
}
