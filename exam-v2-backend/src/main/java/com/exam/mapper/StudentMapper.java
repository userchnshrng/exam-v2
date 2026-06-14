package com.exam.mapper;

import com.exam.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {

    /** 分页 + 搜索（姓名/专业/学院） */
    @Select("""
            <script>
            SELECT studentId, studentName, grade, major, clazz, institute, tel, email, cardId, sex, role
            FROM student
            <where>
                <if test="keyword != null and keyword != ''">
                    studentName LIKE CONCAT('%', #{keyword}, '%')
                    OR major LIKE CONCAT('%', #{keyword}, '%')
                    OR institute LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY studentId ASC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<Student> list(@Param("keyword") String keyword,
                       @Param("offset") int offset,
                       @Param("size") int size);

    /** 统计总数 */
    @Select("""
            <script>
            SELECT COUNT(*) FROM student
            <where>
                <if test="keyword != null and keyword != ''">
                    studentName LIKE CONCAT('%', #{keyword}, '%')
                    OR major LIKE CONCAT('%', #{keyword}, '%')
                    OR institute LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>
            """)
    long count(@Param("keyword") String keyword);

    /** 按 ID 查单条（不含密码） */
    @Select("SELECT studentId, studentName, grade, major, clazz, institute, tel, email, cardId, sex, role FROM student WHERE studentId = #{studentId}")
    Student findById(@Param("studentId") Integer studentId);

    /** 新增 */
    @Insert("INSERT INTO student (studentName, grade, major, clazz, institute, tel, email, pwd, cardId, sex, role) " +
            "VALUES (#{studentName}, #{grade}, #{major}, #{clazz}, #{institute}, #{tel}, #{email}, #{pwd}, #{cardId}, #{sex}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "studentId")
    int insert(Student student);

    /** 更新（不含密码） */
    @Update("UPDATE student SET studentName=#{studentName}, grade=#{grade}, major=#{major}, clazz=#{clazz}, " +
            "institute=#{institute}, tel=#{tel}, email=#{email}, cardId=#{cardId}, sex=#{sex} " +
            "WHERE studentId=#{studentId}")
    int update(Student student);

    /** 单独更新密码 */
    @Update("UPDATE student SET pwd=#{pwd} WHERE studentId=#{studentId}")
    int updatePassword(@Param("studentId") Integer studentId, @Param("pwd") String pwd);

    /** 删除 */
    @Delete("DELETE FROM student WHERE studentId = #{studentId}")
    int delete(@Param("studentId") Integer studentId);
}
