package com.exam.service.impl;

import com.exam.config.UserContext;
import com.exam.controller.ScoreController;
import com.exam.controller.ExamAnswerController;
import com.exam.entity.ExamAnswer;
import com.exam.entity.Score;
import com.exam.mapper.ExamAnswerMapper;
import com.exam.mapper.ScoreMapper;
import com.exam.service.ScoreService;
import com.exam.service.ExamAnswerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreRecordDeletionTest {
    private final ScoreMapper scores = mock(ScoreMapper.class);
    private final ExamAnswerMapper answers = mock(ExamAnswerMapper.class);
    private final ScoreServiceImpl service = new ScoreServiceImpl(scores, answers);

    @AfterEach void clearContext() { UserContext.clear(); }

    @Test void deletingOneAttemptPreservesSameExamOtherAttemptAndOtherExam() {
        List<Score> records = new ArrayList<>(List.of(score(1, 200), score(2, 200), score(3, 201)));
        when(scores.findById(1)).thenReturn(records.get(0));
        when(scores.deleteById(1)).thenAnswer(call -> records.removeIf(s -> s.getScoreId() == 1) ? 1 : 0);
        when(scores.countByExamAndStudent(200, 100)).thenAnswer(call -> records.stream()
                .filter(s -> s.getExamCode() == 200 && s.getStudentId() == 100).count());

        service.deleteById(1);

        assertEquals(List.of(2, 3), records.stream().map(Score::getScoreId).toList());
        verify(scores, never()).deleteByExamAndStudent(anyInt(), anyInt());
        verifyNoInteractions(answers);
    }

    @Test void lastAttemptAlsoCleansOnlyItsExamAnswers() {
        when(scores.findById(1)).thenReturn(score(1, 200));
        when(scores.deleteById(1)).thenReturn(1);
        when(scores.countByExamAndStudent(200, 100)).thenReturn(0L);
        service.deleteById(1);
        verify(answers).deleteByExamAndStudent(200, 100);
        verifyNoMoreInteractions(answers);
    }

    @Test void missingOrConcurrentlyDeletedRecordDoesNotDeleteAnyAnswers() {
        assertThrows(RuntimeException.class, () -> service.deleteById(99));
        when(scores.findById(1)).thenReturn(score(1, 200));
        when(scores.deleteById(1)).thenReturn(0);
        assertThrows(RuntimeException.class, () -> service.deleteById(1));
        verifyNoInteractions(answers);
    }

    @Test void unscoredCleanupRefusesToDeleteAnswersIfScoresExist() {
        when(scores.countByExamAndStudent(200, 100)).thenReturn(2L);
        assertThrows(RuntimeException.class, () -> service.deleteUnscoredAnswers(200, 100));
        verifyNoInteractions(answers);
    }

    @Test void onlyAdminCanUseEveryScoreAndAnswerDeletionEndpoint() {
        ScoreService operations = mock(ScoreService.class);
        ExamAnswerService answerOperations = mock(ExamAnswerService.class);
        ScoreController controller = new ScoreController(operations);
        ExamAnswerController answerController = new ExamAnswerController(answerOperations, operations);
        for (String role : new String[]{null, "STUDENT", "TEACHER"}) {
            UserContext.set("100", role, null);
            assertThrows(RuntimeException.class, () -> controller.deleteById(1));
            assertThrows(RuntimeException.class, () -> controller.deleteByExam(200, 100));
            assertThrows(RuntimeException.class, () -> controller.deleteUnscoredAnswers(100, 200));
            assertThrows(RuntimeException.class, () -> answerController.resetExamRecord(200, 100));
        }
        verifyNoInteractions(operations, answerOperations);
        UserContext.set("1", "ADMIN", "admin");
        controller.deleteById(2);
        controller.deleteByExam(201, 100);
        controller.deleteUnscoredAnswers(100, 202);
        answerController.resetExamRecord(203, 100);
        controller.listStudentExamRecords(100);
        verify(operations).deleteById(2);
        verify(operations).deleteByExamAndStudent(201, 100);
        verify(operations).deleteUnscoredAnswers(202, 100);
        verify(answerOperations).deleteByExamAndStudent(203, 100);
        verify(operations).deleteByExamAndStudent(203, 100);
        verify(operations).listStudentExamRecords(100);
    }

    @Test void teacherRetainsViewAndUpdateAccessAcrossStudentsAndExams() {
        ScoreService operations = mock(ScoreService.class);
        ExamAnswerService answerOperations = mock(ExamAnswerService.class);
        ScoreController controller = new ScoreController(operations);
        ExamAnswerController answerController = new ExamAnswerController(answerOperations, operations);
        UserContext.set("1", "TEACHER", "teacher");

        for (int studentId : new int[]{100, 101}) {
            for (int examCode : new int[]{200, 201}) {
                controller.list(studentId, 1, 10);
                answerController.getByExamAndStudent(examCode, studentId);
                verify(answerOperations).getByExamAndStudent(examCode, studentId);
            }
        }
        Score updatedScore = score(1, 200);
        when(operations.getById(1)).thenReturn(updatedScore);
        controller.getById(1);
        controller.update(1, updatedScore);
        verify(operations).updateScore(updatedScore);

        ExamAnswer updatedAnswer = new ExamAnswer();
        updatedAnswer.setScore(2);
        answerController.updateAnswer(10, updatedAnswer);
        assertEquals(10, updatedAnswer.getAnswerId());
        verify(answerOperations).updateAnswer(updatedAnswer);
    }

    private Score score(int id, int examCode) {
        Score result = new Score();
        result.setScoreId(id); result.setExamCode(examCode); result.setStudentId(100);
        result.setSubject("计算机网络");
        return result;
    }
}
