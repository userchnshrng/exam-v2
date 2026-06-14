-- ============================================================
-- 第五阶段迁移：新增答题记录表
-- 在 exam 数据库中执行此 SQL
-- ============================================================

DROP TABLE IF EXISTS `exam_answer`;
CREATE TABLE `exam_answer` (
    `answerId` int NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
    `examCode` int DEFAULT NULL COMMENT '考试编号',
    `studentId` int DEFAULT NULL COMMENT '学生学号',
    `questionType` int DEFAULT NULL COMMENT '题目类型(1选择题 2填空题 3判断题)',
    `questionId` int DEFAULT NULL COMMENT '题目编号',
    `studentAnswer` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学生提交的答案',
    `correctAnswer` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '正确答案',
    `isCorrect` tinyint(1) DEFAULT 0 COMMENT '是否正确 0错误 1正确',
    `score` int DEFAULT 0 COMMENT '该题实际得分',
    PRIMARY KEY (`answerId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='答题记录表';
