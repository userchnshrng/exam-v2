package com.exam.mapper;

import com.exam.entity.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公告 Mapper — 全注解方式，与旧项目风格一致
 */
@Mapper
public interface NoticeMapper {

    /** 分页查询 + 关键词搜索 */
    @Select("""
            <script>
            SELECT * FROM notice
            <where>
                <if test="keyword != null and keyword != ''">
                    content LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY noticeId DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<Notice> list(@Param("keyword") String keyword,
                      @Param("offset") int offset,
                      @Param("size") int size);

    /** 统计总数 */
    @Select("""
            <script>
            SELECT COUNT(*) FROM notice
            <where>
                <if test="keyword != null and keyword != ''">
                    content LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>
            """)
    long count(@Param("keyword") String keyword);

    /** 按 ID 查单条 */
    @Select("SELECT * FROM notice WHERE noticeId = #{noticeId}")
    Notice findById(@Param("noticeId") Integer noticeId);

    /** 新增公告 */
    @Insert("INSERT INTO notice (content, createTime) VALUES (#{content}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "noticeId")
    int insert(Notice notice);

    /** 更新公告 */
    @Update("UPDATE notice SET content = #{content}, createTime = #{createTime} WHERE noticeId = #{noticeId}")
    int update(Notice notice);

    /** 删除公告 */
    @Delete("DELETE FROM notice WHERE noticeId = #{noticeId}")
    int delete(@Param("noticeId") Integer noticeId);
}
