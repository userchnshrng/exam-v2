package com.exam.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaperManageMapper {

    /** 获取试卷的所有题目映射 */
    @Select("SELECT * FROM paper_manage WHERE paperId = #{paperId}")
    List<Map<String, Object>> listByPaperId(@Param("paperId") Integer paperId);

    /** 添加题目到试卷 */
    @Insert("INSERT INTO paper_manage (paperId, questionType, questionId) VALUES (#{paperId}, #{questionType}, #{questionId})")
    int insertMapping(@Param("paperId") Integer paperId,
                      @Param("questionType") Integer questionType,
                      @Param("questionId") Integer questionId);

    /** 从试卷移除题目 */
    @Delete("DELETE FROM paper_manage WHERE paperId = #{paperId} AND questionType = #{questionType} AND questionId = #{questionId}")
    int deleteMapping(@Param("paperId") Integer paperId,
                      @Param("questionType") Integer questionType,
                      @Param("questionId") Integer questionId);

    /** 清空试卷所有题目 */
    @Delete("DELETE FROM paper_manage WHERE paperId = #{paperId}")
    int clearByPaperId(@Param("paperId") Integer paperId);

    /** 按科目查选择题 */
    @Select("SELECT * FROM multi_question WHERE TRIM(subject) = #{subject} ORDER BY questionId")
    List<Map<String, Object>> listMultiBySubject(@Param("subject") String subject);

    /** 按科目查填空题 */
    @Select("SELECT * FROM fill_question WHERE TRIM(subject) = #{subject} ORDER BY questionId")
    List<Map<String, Object>> listFillBySubject(@Param("subject") String subject);

    /** 按科目查判断题 */
    @Select("SELECT * FROM judge_question WHERE TRIM(subject) = #{subject} ORDER BY questionId")
    List<Map<String, Object>> listJudgeBySubject(@Param("subject") String subject);

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
