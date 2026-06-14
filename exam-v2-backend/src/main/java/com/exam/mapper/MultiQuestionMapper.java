package com.exam.mapper;

import com.exam.entity.MultiQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MultiQuestionMapper {

    @Select("""
            <script>
            SELECT * FROM multi_question
            <where>
                <if test="keyword != null and keyword != ''">
                    question LIKE CONCAT('%', #{keyword}, '%')
                    OR subject LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY questionId ASC
            LIMIT #{offset}, #{size}
            </script>""")
    List<MultiQuestion> list(@Param("keyword") String keyword,
                             @Param("offset") int offset,
                             @Param("size") int size);

    @Select("""
            <script>
            SELECT COUNT(*) FROM multi_question
            <where>
                <if test="keyword != null and keyword != ''">
                    question LIKE CONCAT('%', #{keyword}, '%')
                    OR subject LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>""")
    long count(@Param("keyword") String keyword);

    @Select("SELECT * FROM multi_question WHERE questionId = #{questionId}")
    MultiQuestion findById(@Param("questionId") Integer questionId);

    @Insert("INSERT INTO multi_question (subject, question, answerA, answerB, answerC, answerD, rightAnswer, analysis, score, section, level) " +
            "VALUES (#{subject}, #{question}, #{answerA}, #{answerB}, #{answerC}, #{answerD}, #{rightAnswer}, #{analysis}, #{score}, #{section}, #{level})")
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(MultiQuestion q);

    @Update("UPDATE multi_question SET subject=#{subject}, question=#{question}, answerA=#{answerA}, answerB=#{answerB}, " +
            "answerC=#{answerC}, answerD=#{answerD}, rightAnswer=#{rightAnswer}, analysis=#{analysis}, " +
            "score=#{score}, section=#{section}, level=#{level} WHERE questionId=#{questionId}")
    int update(MultiQuestion q);

    @Delete("DELETE FROM multi_question WHERE questionId = #{questionId}")
    int delete(@Param("questionId") Integer questionId);
}
