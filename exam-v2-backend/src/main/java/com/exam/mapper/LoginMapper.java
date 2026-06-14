package com.exam.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface LoginMapper {

    @Select("""
            SELECT COLUMN_NAME
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = 'exam'
              AND TABLE_NAME = #{tableName}
            ORDER BY ORDINAL_POSITION
            """)
    List<String> listColumns(@Param("tableName") String tableName);

    @Select("""
            SELECT *
            FROM ${tableName}
            WHERE ${columnName} = #{value}
            LIMIT 1
            """)
    Map<String, Object> findOneByColumn(@Param("tableName") String tableName,
                                        @Param("columnName") String columnName,
                                        @Param("value") String value);
}
