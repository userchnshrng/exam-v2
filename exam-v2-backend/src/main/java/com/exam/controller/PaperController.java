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
}
