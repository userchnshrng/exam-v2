package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.dto.SubmitAnswerRequest;
import com.exam.entity.ExamManage;
import com.exam.mapper.ExamManageMapper;
import com.exam.service.PaperService;
import com.exam.vo.ExamQuestionVO;
import com.exam.vo.SubmitResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paper")
public class PaperController {

    private final PaperService paperService;
    private final ExamManageMapper examManageMapper;

    public PaperController(PaperService paperService, ExamManageMapper examManageMapper) {
        this.paperService = paperService;
        this.examManageMapper = examManageMapper;
    }

    /** 获取某场考试的全部题目（不含正确答案） */
    @GetMapping("/{examCode}/questions")
    public ApiResponse<List<ExamQuestionVO>> getQuestions(@PathVariable("examCode") Integer examCode) {
        ExamManage exam = examManageMapper.findById(examCode);
        if (exam == null) return ApiResponse.fail("考试不存在");
        if (exam.getPaperId() == null) return ApiResponse.fail("该考试尚未关联试卷");
        List<ExamQuestionVO> questions = paperService.getPaperQuestions(exam.getPaperId());
        return ApiResponse.success(questions);
    }

    /** 提交试卷 */
    @PostMapping("/{examCode}/submit")
    public ApiResponse<SubmitResultVO> submit(@PathVariable("examCode") Integer examCode,
                                              @RequestBody SubmitAnswerRequest request) {
        request.setExamCode(examCode);
        SubmitResultVO result = paperService.submitAnswers(request);
        return ApiResponse.success("提交成功", result);
    }

    /** 查看答题详情 */
    @GetMapping("/{examCode}/answers/{studentId}")
    public ApiResponse<List<SubmitResultVO.AnswerResult>> getAnswers(
            @PathVariable("examCode") Integer examCode,
            @PathVariable("studentId") Integer studentId) {
        return ApiResponse.success(paperService.getAnswerDetails(examCode, studentId));
    }

    // ---- 组卷（题目-试卷关联管理） ----

    /** 获取组卷数据 */
    @GetMapping("/compose/{paperId}")
    public ApiResponse<List<Map<String, Object>>> getComposeData(
            @PathVariable Integer paperId,
            @RequestParam String subject) {
        return ApiResponse.success(paperService.getComposeData(paperId, subject));
    }

    /** 添加题目到试卷 */
    @PostMapping("/compose/{paperId}/add")
    public ApiResponse<Void> addQuestion(
            @PathVariable Integer paperId,
            @RequestBody Map<String, Object> body) {
        Integer questionType = (Integer) body.get("questionType");
        Integer questionId = (Integer) body.get("questionId");
        paperService.addQuestionToPaper(paperId, questionType, questionId);
        return ApiResponse.success(null);
    }

    /** 一键自动组卷 */
    @PostMapping("/compose/{paperId}/auto")
    public ApiResponse<Map<String, Object>> autoCompose(
            @PathVariable Integer paperId,
            @RequestParam String subject) {
        int added = paperService.autoCompose(paperId, subject.trim());
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("added", added);
        result.put("message", added > 0 ? "成功添加 " + added + " 道题目" : "未发现新题目，该科目下题目可能已全部加入或题库为空");
        return ApiResponse.success(result);
    }

    /** 从试卷移除题目 */
    @PostMapping("/compose/{paperId}/remove")
    public ApiResponse<Void> removeQuestion(
            @PathVariable Integer paperId,
            @RequestBody Map<String, Object> body) {
        Integer questionType = (Integer) body.get("questionType");
        Integer questionId = (Integer) body.get("questionId");
        paperService.removeQuestionFromPaper(paperId, questionType, questionId);
        return ApiResponse.success(null);
    }
}
