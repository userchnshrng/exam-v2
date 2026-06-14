package com.exam.mapper;

import com.exam.entity.FillQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FillQuestionMapper {

    @Select("""
            <script>
            SELECT * FROM fill_question
            <where>
                <if test="keyword != null and keyword != ''">
                    question LIKE CONCAT('%', #{keyword}, '%')
                    OR subject LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY questionId ASC
            LIMIT #{offset}, #{size}
            </script>""")
    List<FillQuestion> list(@Param("keyword") String keyword,
                            @Param("offset") int offset,
                            @Param("size") int size);

    @Select("""
            <script>
            SELECT COUNT(*) FROM fill_question
            <where>
                <if test="keyword != null and keyword != ''">
                    question LIKE CONCAT('%', #{keyword}, '%')
                    OR subject LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>""")
    long count(@Param("keyword") String keyword);

    @Select("SELECT * FROM fill_question WHERE questionId = #{questionId}")
    FillQuestion findById(@Param("questionId") Integer questionId);

    @Insert("INSERT INTO fill_question (subject, question, answer, analysis, score, level, section) " +
            "VALUES (#{subject}, #{question}, #{answer}, #{analysis}, #{score}, #{level}, #{section})")
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(FillQuestion q);

    @Update("UPDATE fill_question SET subject=#{subject}, question=#{question}, answer=#{answer}, " +
            "analysis=#{analysis}, score=#{score}, level=#{level}, section=#{section} WHERE questionId=#{questionId}")
    int update(FillQuestion q);

    @Delete("DELETE FROM fill_question WHERE questionId = #{questionId}")
    int delete(@Param("questionId") Integer questionId);
}
