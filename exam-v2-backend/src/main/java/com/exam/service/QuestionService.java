package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.FillQuestion;
import com.exam.entity.JudgeQuestion;
import com.exam.entity.MultiQuestion;

public interface QuestionService {

    // ---- 选择题 ----
    PageResult<MultiQuestion> listMulti(String keyword, int page, int size);
    MultiQuestion getMultiById(Integer id);
    MultiQuestion createMulti(MultiQuestion q);
    MultiQuestion updateMulti(MultiQuestion q);
    void deleteMulti(Integer id);

    // ---- 填空题 ----
    PageResult<FillQuestion> listFill(String keyword, int page, int size);
    FillQuestion getFillById(Integer id);
    FillQuestion createFill(FillQuestion q);
    FillQuestion updateFill(FillQuestion q);
    void deleteFill(Integer id);

    // ---- 判断题 ----
    PageResult<JudgeQuestion> listJudge(String keyword, int page, int size);
    JudgeQuestion getJudgeById(Integer id);
    JudgeQuestion createJudge(JudgeQuestion q);
    JudgeQuestion updateJudge(JudgeQuestion q);
    void deleteJudge(Integer id);
}
