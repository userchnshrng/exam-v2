-- 用途：每次交卷以 score.scoreId 区分，防止同一考试的多次答卷混入详情。
-- 执行前备份数据库；本脚本只需执行一次，不删除、不覆盖历史成绩或答卷。
-- 如果结构中已存在 scoreId 列或 idx_exam_answer_scoreId 索引，请勿重复执行。
ALTER TABLE `exam_answer`
  ADD COLUMN `scoreId` int NULL DEFAULT NULL COMMENT '本次交卷对应的成绩编号；NULL表示未关联的历史答卷',
  ADD INDEX `idx_exam_answer_scoreId` (`scoreId`);

-- 历史答卷维持 NULL，不根据行号、答题日期或得分猜测所属次数。
-- 历史详情会明确提示无法确定所属次数，历史成绩仍可查询和导出。
