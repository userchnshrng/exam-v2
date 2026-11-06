package com.exam.service.impl;

import com.exam.config.UserContext;
import com.exam.controller.ExamAnswerController;
import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.ScoreService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamAttemptDetailsTest {
    private final ExamAnswerMapper answers = mock(ExamAnswerMapper.class);
    private final ScoreMapper scores = mock(ScoreMapper.class);
    private final ExamAnswerServiceImpl service = new ExamAnswerServiceImpl(answers,
            mock(MultiQuestionMapper.class), mock(FillQuestionMapper.class), mock(JudgeQuestionMapper.class), scores);

    @AfterEach void clearContext() { UserContext.clear(); }

    @Test void studentAndTeacherSeeOnlyTheClickedBlankAttempt() {
        Score attempt = score(12, 100);
        ExamAnswer blank = answer(12, "", false, 0);
        when(scores.findById(12)).thenReturn(attempt);
        when(answers.findByScoreId(12)).thenReturn(List.of(blank));
        ExamAnswerController controller = new ExamAnswerController(service, mock(ScoreService.class));
        UserContext.set("100", "STUDENT", "student");
        var student = controller.getByExam(200, 12).getData();
        UserContext.set("1", "TEACHER", "teacher");
        var teacher = controller.getByExamAndStudent(200, 100, 12).getData();
        for (var details : List.of(student, teacher)) {
            assertEquals(1, details.size()); assertEquals(12, details.get(0).getScoreId());
            assertEquals("", details.get(0).getStudentAnswer());
            assertFalse(details.get(0).getIsCorrect()); assertEquals(0, details.get(0).getScore());
        }
        verify(answers, never()).findByExamAndStudent(anyInt(), anyInt());
    }

    @Test void scoreIdMustBelongToTheExamAndTheStudent() {
        when(scores.findById(12)).thenReturn(score(12, 101));
        assertThrows(RuntimeException.class, () -> service.getByExamAndStudent(200, 100, 12));
        verifyNoInteractions(answers);
    }

    @Test void ambiguousLegacyAnswersAreNotShownAsTheClickedAttempt() {
        when(scores.findById(12)).thenReturn(score(12, 100));
        when(answers.findByScoreId(12)).thenReturn(List.of());
        var error = assertThrows(RuntimeException.class, () -> service.getByExamAndStudent(200, 100, 12));
        assertTrue(error.getMessage().contains("历史答卷"));
        verify(answers, never()).findByExamAndStudent(anyInt(), anyInt());
    }

    @Test void malformedStoredBlankAnswerCannotAppearCorrectOrHavePoints() {
        when(scores.findById(12)).thenReturn(score(12, 100));
        when(answers.findByScoreId(12)).thenReturn(List.of(answer(12, " \t", true, 9)));
        var detail = service.getByExamAndStudent(200, 100, 12).get(0);
        assertFalse(detail.getIsCorrect()); assertEquals(0, detail.getScore());
    }

    @Test void teacherClearingAnAnswerResetsItsPointsAndOnlyRecalculatesThatAttempt() {
        ExamAnswer existing = answer(12, "A", true, 2);
        when(answers.findById(5)).thenReturn(existing);
        when(answers.findByScoreId(12)).thenReturn(List.of(existing));
        ExamAnswer changed = new ExamAnswer();
        changed.setAnswerId(5); changed.setStudentAnswer(" "); changed.setIsCorrect(true); changed.setScore(100);
        service.updateAnswer(changed);
        assertFalse(existing.getIsCorrect()); assertEquals(0, existing.getScore());
        verify(scores).updateEtScore(12, 0);
        verify(answers, never()).findByExamAndStudent(anyInt(), anyInt());
    }

    @Test void teacherRescoringKeepsTheAttemptAndRecalculatesAllItsAnsweredQuestions() {
        ExamAnswer existing = answer(12, "A", true, 2);
        ExamAnswer other = answer(12, "T", true, 3);
        other.setAnswerId(6);
        ExamAnswer blank = answer(12, "", true, 50);
        blank.setAnswerId(7);
        when(answers.findById(5)).thenReturn(existing);
        when(answers.findByScoreId(12)).thenReturn(List.of(existing, other, blank));
        ExamAnswer changed = answer(11, "B", false, 1);
        changed.setExamCode(999); changed.setStudentId(999);
        service.updateAnswer(changed);
        assertEquals(12, existing.getScoreId());
        assertEquals(200, existing.getExamCode()); assertEquals(100, existing.getStudentId());
        assertEquals("B", existing.getStudentAnswer());
        verify(answers).update(existing);
        verify(scores).updateEtScore(12, 4);
        verifyNoMoreInteractions(scores);
    }

    private Score score(int id, int studentId) {
        Score score = new Score(); score.setScoreId(id); score.setExamCode(200); score.setStudentId(studentId); return score;
    }

    private ExamAnswer answer(int id, String text, boolean correct, int points) {
        ExamAnswer answer = new ExamAnswer(); answer.setAnswerId(5); answer.setScoreId(id);
        answer.setExamCode(200); answer.setStudentId(100); answer.setQuestionType(1); answer.setQuestionId(1);
        answer.setStudentAnswer(text); answer.setIsCorrect(correct); answer.setScore(points); return answer;
    }
}
