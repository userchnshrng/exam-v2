package com.exam.mapper;

import com.exam.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("SELECT * FROM message ORDER BY id DESC LIMIT #{offset}, #{size}")
    List<Message> list(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM message")
    long count();

    @Select("SELECT * FROM message WHERE id = #{id}")
    Message findById(@Param("id") Integer id);

    @Insert("INSERT INTO message (title, content, time) VALUES (#{title}, #{content}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);
}
