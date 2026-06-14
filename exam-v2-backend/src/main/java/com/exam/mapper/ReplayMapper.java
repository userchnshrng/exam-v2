package com.exam.mapper;

import com.exam.entity.Replay;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReplayMapper {

    @Select("SELECT * FROM replay WHERE messageId = #{messageId} ORDER BY replayId ASC")
    List<Replay> findByMessageId(@Param("messageId") Integer messageId);

    @Insert("INSERT INTO replay (messageId, replay, replayTime) VALUES (#{messageId}, #{replay}, #{replayTime})")
    @Options(useGeneratedKeys = true, keyProperty = "replayId")
    int insert(Replay replay);
}
