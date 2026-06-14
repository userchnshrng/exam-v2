package com.exam.mapper;

import com.exam.entity.ExamManage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamManageMapper {

    /** 分页 + 搜索（按课程名/描述/专业） */
    @Select("""
            <script>
            SELECT * FROM exam_manage
            <where>
                <if test="keyword != null and keyword != ''">
                    source LIKE CONCAT('%', #{keyword}, '%')
                    OR description LIKE CONCAT('%', #{keyword}, '%')
                    OR major LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY examCode DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<ExamManage> list(@Param("keyword") String keyword,
                          @Param("offset") int offset,
                          @Param("size") int size);

    @Select("""
            <script>
            SELECT COUNT(*) FROM exam_manage
            <where>
                <if test="keyword != null and keyword != ''">
                    source LIKE CONCAT('%', #{keyword}, '%')
                    OR description LIKE CONCAT('%', #{keyword}, '%')
                    OR major LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>
            """)
    long count(@Param("keyword") String keyword);

    @Select("SELECT * FROM exam_manage WHERE examCode = #{examCode}")
    ExamManage findById(@Param("examCode") Integer examCode);

    @Insert("INSERT INTO exam_manage (description, source, paperId, examDate, totalTime, grade, term, major, institute, totalScore, type, tips) " +
            "VALUES (#{description}, #{source}, #{paperId}, #{examDate}, #{totalTime}, #{grade}, #{term}, #{major}, #{institute}, #{totalScore}, #{type}, #{tips})")
    @Options(useGeneratedKeys = true, keyProperty = "examCode")
    int insert(ExamManage exam);

    @Update("UPDATE exam_manage SET description=#{description}, source=#{source}, paperId=#{paperId}, " +
            "examDate=#{examDate}, totalTime=#{totalTime}, grade=#{grade}, term=#{term}, major=#{major}, " +
            "institute=#{institute}, totalScore=#{totalScore}, type=#{type}, tips=#{tips} " +
            "WHERE examCode=#{examCode}")
    int update(ExamManage exam);

    @Delete("DELETE FROM exam_manage WHERE examCode = #{examCode}")
    int delete(@Param("examCode") Integer examCode);
}
