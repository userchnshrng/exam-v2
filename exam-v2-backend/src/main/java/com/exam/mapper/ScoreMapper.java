package com.exam.mapper;

import com.exam.entity.Score;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScoreMapper {

    @Select("""
            <script>
            SELECT * FROM score
            <where>
                <if test="studentId != null">studentId = #{studentId}</if>
            </where>
            ORDER BY scoreId DESC
            LIMIT #{offset}, #{size}
            </script>""")
    List<Score> list(@Param("studentId") Integer studentId,
                     @Param("offset") int offset,
                     @Param("size") int size);

    @Select("""
            <script>
            SELECT COUNT(*) FROM score
            <where>
                <if test="studentId != null">studentId = #{studentId}</if>
            </where>
            </script>""")
    long count(@Param("studentId") Integer studentId);

    @Select("SELECT * FROM score WHERE scoreId = #{scoreId}")
    Score findById(@Param("scoreId") Integer scoreId);

    @Insert("INSERT INTO score (examCode, studentId, subject, etScore, answerDate) " +
            "VALUES (#{examCode}, #{studentId}, #{subject}, #{etScore}, #{answerDate})")
    @Options(useGeneratedKeys = true, keyProperty = "scoreId")
    int insert(Score score);
}
