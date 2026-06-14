package com.exam.mapper;

import com.exam.entity.Score;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    /** 各考试平均分/最高分/最低分对比 — 一条 SQL 完成 LEFT JOIN + 聚合 */
    @Select("""
            SELECT
                s.examCode      AS examCode,
                CASE
                    WHEN e.description IS NOT NULL AND e.source IS NOT NULL
                        THEN CONCAT(e.source, ' ', e.description)
                    WHEN e.description IS NOT NULL THEN e.description
                    WHEN e.source IS NOT NULL THEN e.source
                    ELSE CONCAT('考试', s.examCode)
                END             AS examName,
                ROUND(AVG(s.etScore), 1) AS avgScore,
                MAX(s.etScore)  AS maxScore,
                MIN(s.etScore)  AS minScore,
                COUNT(*)        AS count
            FROM score s
            LEFT JOIN exam_manage e ON s.examCode = e.examCode
            WHERE s.etScore IS NOT NULL
            GROUP BY s.examCode, e.source, e.description
            ORDER BY s.examCode
            """)
    List<Map<String, Object>> examComparison();
}
