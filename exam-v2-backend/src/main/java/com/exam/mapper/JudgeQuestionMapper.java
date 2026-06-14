package com.exam.mapper;

import com.exam.entity.JudgeQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JudgeQuestionMapper {

    @Select("""
            <script>
            SELECT * FROM judge_question
            <where>
                <if test="keyword != null and keyword != ''">
                    question LIKE CONCAT('%', #{keyword}, '%')
                    OR subject LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY questionId ASC
            LIMIT #{offset}, #{size}
            </script>""")
    List<JudgeQuestion> list(@Param("keyword") String keyword,
                             @Param("offset") int offset,
                             @Param("size") int size);

    @Select("""
            <script>
            SELECT COUNT(*) FROM judge_question
            <where>
                <if test="keyword != null and keyword != ''">
                    question LIKE CONCAT('%', #{keyword}, '%')
                    OR subject LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>""")
    long count(@Param("keyword") String keyword);

    @Select("SELECT * FROM judge_question WHERE questionId = #{questionId}")
    JudgeQuestion findById(@Param("questionId") Integer questionId);

    @Insert("INSERT INTO judge_question (subject, question, answer, analysis, score, level, section) " +
            "VALUES (#{subject}, #{question}, #{answer}, #{analysis}, #{score}, #{level}, #{section})")
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(JudgeQuestion q);

    @Update("UPDATE judge_question SET subject=#{subject}, question=#{question}, answer=#{answer}, " +
            "analysis=#{analysis}, score=#{score}, level=#{level}, section=#{section} WHERE questionId=#{questionId}")
    int update(JudgeQuestion q);

    @Delete("DELETE FROM judge_question WHERE questionId = #{questionId}")
    int delete(@Param("questionId") Integer questionId);
}
