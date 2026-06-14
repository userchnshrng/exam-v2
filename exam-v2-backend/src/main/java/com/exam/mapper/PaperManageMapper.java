package com.exam.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaperManageMapper {

    /** 获取试卷的所有题目映射 */
    @Select("SELECT * FROM paper_manage WHERE paperId = #{paperId}")
    List<Map<String, Object>> listByPaperId(@Param("paperId") Integer paperId);

    /** 查选择题 */
    @Select("SELECT * FROM multi_question WHERE questionId = #{questionId}")
    Map<String, Object> findMultiById(@Param("questionId") Integer questionId);

    /** 查填空题 */
    @Select("SELECT * FROM fill_question WHERE questionId = #{questionId}")
    Map<String, Object> findFillById(@Param("questionId") Integer questionId);

    /** 查判断题 */
    @Select("SELECT * FROM judge_question WHERE questionId = #{questionId}")
    Map<String, Object> findJudgeById(@Param("questionId") Integer questionId);
}
