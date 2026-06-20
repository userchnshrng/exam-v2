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

    /** 按考试编号取全部成绩（用于单场考试统计） */
    @Select("SELECT * FROM score WHERE examCode = #{examCode}")
    List<Score> listByExamCode(@Param("examCode") Integer examCode);

    /** 按考试编号查学生成绩 — 联表 student 带出姓名 */
    @Select("""
            SELECT s.*, st.studentName
            FROM score s
            LEFT JOIN student st ON s.studentId = st.studentId
            WHERE s.examCode = #{examCode}
            ORDER BY s.etScore DESC
            """)
    List<Map<String, Object>> listStudentScoresByExamCode(@Param("examCode") Integer examCode);

    @Insert("INSERT INTO score (examCode, studentId, subject, etScore, answerDate) " +
            "VALUES (#{examCode}, #{studentId}, #{subject}, #{etScore}, #{answerDate})")
    @Options(useGeneratedKeys = true, keyProperty = "scoreId")
    int insert(Score score);

    @Delete("DELETE FROM score WHERE examCode = #{examCode} AND studentId = #{studentId}")
    int deleteByExamAndStudent(@Param("examCode") Integer examCode,
                               @Param("studentId") Integer studentId);

    @Update("UPDATE score SET etScore=#{etScore}, score=#{score}, ptScore=#{ptScore} WHERE scoreId=#{scoreId}")
    int update(Score score);

    /**
     * 各考试平均分/最高分/最低分对比 — 一条 SQL 完成 LEFT JOIN + 聚合。
     * 三级回退取考试名：
     *   ① exam_manage 有匹配 → source + description（如"计算机网络 2019年上期期末考试"）
     *   ② exam_manage 无匹配 → score.subject（如"计算机网络"），附加 examCode 供辨识
     *   ③ 两者都无 → 仅 examCode 兜底
     * 用 MAX() 包裹非分组列，兼容 MySQL ONLY_FULL_GROUP_BY 模式。
     */
    @Select("""
            SELECT
                s.examCode      AS examCode,
                CASE
                    WHEN MAX(e.source) IS NOT NULL AND MAX(e.description) IS NOT NULL
                        THEN CONCAT(MAX(e.source), ' ', MAX(e.description))
                    WHEN MAX(e.source) IS NOT NULL
                        THEN MAX(e.source)
                    WHEN MAX(e.description) IS NOT NULL
                        THEN MAX(e.description)
                    WHEN MAX(s.subject) IS NOT NULL AND MAX(s.subject) != ''
                        THEN CONCAT(MAX(s.subject), ' (考试', s.examCode, ')')
                    ELSE CONCAT('考试', s.examCode)
                END             AS examName,
                ROUND(AVG(s.etScore), 1) AS avgScore,
                MAX(s.etScore)  AS maxScore,
                MIN(s.etScore)  AS minScore,
                COUNT(*)        AS count
            FROM score s
            LEFT JOIN exam_manage e ON s.examCode = e.examCode
            WHERE s.etScore IS NOT NULL
            GROUP BY s.examCode
            ORDER BY s.examCode
            """)
    List<Map<String, Object>> examComparison();
}
