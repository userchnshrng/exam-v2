package com.exam.mapper;

import com.exam.entity.ExamAnswer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamAnswerMapper {

    @Insert("INSERT INTO exam_answer (examCode, studentId, questionType, questionId, studentAnswer, correctAnswer, isCorrect, score) " +
            "VALUES (#{examCode}, #{studentId}, #{questionType}, #{questionId}, #{studentAnswer}, #{correctAnswer}, #{isCorrect}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "answerId")
    int insert(ExamAnswer answer);

    @Select("SELECT * FROM exam_answer WHERE examCode = #{examCode} AND studentId = #{studentId}")
    List<ExamAnswer> findByExamAndStudent(@Param("examCode") Integer examCode,
                                          @Param("studentId") Integer studentId);

    @Delete("DELETE FROM exam_answer WHERE examCode = #{examCode} AND studentId = #{studentId}")
    int deleteByExamAndStudent(@Param("examCode") Integer examCode,
                               @Param("studentId") Integer studentId);

    @Update("UPDATE exam_answer SET studentAnswer=#{studentAnswer}, isCorrect=#{isCorrect}, score=#{score} WHERE answerId=#{answerId}")
    int update(ExamAnswer answer);
}
