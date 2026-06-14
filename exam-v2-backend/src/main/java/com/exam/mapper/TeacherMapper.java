package com.exam.mapper;

import com.exam.entity.Teacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherMapper {

    /** 分页 + 搜索（按姓名或学院） */
    @Select("""
            <script>
            SELECT teacherId, teacherName, institute, sex, tel, email, cardId, type, role
            FROM teacher
            <where>
                <if test="keyword != null and keyword != ''">
                    teacherName LIKE CONCAT('%', #{keyword}, '%')
                    OR institute LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY teacherId ASC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<Teacher> list(@Param("keyword") String keyword,
                       @Param("offset") int offset,
                       @Param("size") int size);

    /** 统计总数 */
    @Select("""
            <script>
            SELECT COUNT(*) FROM teacher
            <where>
                <if test="keyword != null and keyword != ''">
                    teacherName LIKE CONCAT('%', #{keyword}, '%')
                    OR institute LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>
            """)
    long count(@Param("keyword") String keyword);

    /** 按 ID 查单条（不含密码） */
    @Select("SELECT teacherId, teacherName, institute, sex, tel, email, cardId, type, role FROM teacher WHERE teacherId = #{teacherId}")
    Teacher findById(@Param("teacherId") Integer teacherId);

    /** 新增 */
    @Insert("INSERT INTO teacher (teacherName, institute, sex, tel, email, pwd, cardId, type, role) " +
            "VALUES (#{teacherName}, #{institute}, #{sex}, #{tel}, #{email}, #{pwd}, #{cardId}, #{type}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "teacherId")
    int insert(Teacher teacher);

    /** 更新（不含密码 — 密码单独处理） */
    @Update("UPDATE teacher SET teacherName=#{teacherName}, institute=#{institute}, sex=#{sex}, " +
            "tel=#{tel}, email=#{email}, cardId=#{cardId}, type=#{type} " +
            "WHERE teacherId=#{teacherId}")
    int update(Teacher teacher);

    /** 单独更新密码 */
    @Update("UPDATE teacher SET pwd=#{pwd} WHERE teacherId=#{teacherId}")
    int updatePassword(@Param("teacherId") Integer teacherId, @Param("pwd") String pwd);

    /** 删除 */
    @Delete("DELETE FROM teacher WHERE teacherId = #{teacherId}")
    int delete(@Param("teacherId") Integer teacherId);
}
