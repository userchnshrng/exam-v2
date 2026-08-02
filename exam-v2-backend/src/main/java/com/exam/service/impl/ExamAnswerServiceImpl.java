package com.exam.service.impl;

import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.ExamAnswerService;
import com.exam.vo.ExamAnswerVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExamAnswerServiceImpl implements ExamAnswerService {

    private final ExamAnswerMapper examAnswerMapper;
    private final MultiQuestionMapper multiQuestionMapper;
    private final FillQuestionMapper fillQuestionMapper;
    private final JudgeQuestionMapper judgeQuestionMapper;

    @Value("${deepseek.api.key}")
    private String deepseekApiKey;

    @Value("${deepseek.api.base-url}")
    private String deepseekBaseUrl;

    @Value("${deepseek.api.model}")
    private String deepseekModel;

    public ExamAnswerServiceImpl(ExamAnswerMapper examAnswerMapper,
                                 MultiQuestionMapper multiQuestionMapper,
                                 FillQuestionMapper fillQuestionMapper,
                                 JudgeQuestionMapper judgeQuestionMapper) {
        this.examAnswerMapper = examAnswerMapper;
        this.multiQuestionMapper = multiQuestionMapper;
        this.fillQuestionMapper = fillQuestionMapper;
        this.judgeQuestionMapper = judgeQuestionMapper;
    }

    @Override
    public List<ExamAnswerVO> getByExamAndStudent(Integer examCode, Integer studentId) {
        List<ExamAnswer> answers = examAnswerMapper.findByExamAndStudent(examCode, studentId);
        List<ExamAnswerVO> result = new ArrayList<>();

        for (ExamAnswer a : answers) {
            ExamAnswerVO vo = new ExamAnswerVO();
            vo.setAnswerId(a.getAnswerId());
            vo.setExamCode(a.getExamCode());
            vo.setStudentId(a.getStudentId());
            vo.setQuestionType(a.getQuestionType());
            vo.setQuestionId(a.getQuestionId());
            vo.setStudentAnswer(a.getStudentAnswer());
            vo.setCorrectAnswer(a.getCorrectAnswer());
            vo.setIsCorrect(a.getIsCorrect());
            vo.setScore(a.getScore());

            // 根据题型查题库，填入题干内容
            if (a.getQuestionType() != null) {
                switch (a.getQuestionType()) {
                    case 1 -> { // 选择题
                        MultiQuestion mq = multiQuestionMapper.findById(a.getQuestionId());
                        if (mq != null) {
                            vo.setQuestionContent(mq.getQuestion());
                            vo.setOptionA("A. " + (mq.getAnswerA() != null ? mq.getAnswerA() : ""));
                            vo.setOptionB("B. " + (mq.getAnswerB() != null ? mq.getAnswerB() : ""));
                            vo.setOptionC("C. " + (mq.getAnswerC() != null ? mq.getAnswerC() : ""));
                            vo.setOptionD("D. " + (mq.getAnswerD() != null ? mq.getAnswerD() : ""));
                        }
                    }
                    case 2 -> { // 填空题
                        FillQuestion fq = fillQuestionMapper.findById(a.getQuestionId());
                        if (fq != null) {
                            vo.setQuestionContent(fq.getQuestion());
                        }
                    }
                    case 3 -> { // 判断题
                        JudgeQuestion jq = judgeQuestionMapper.findById(a.getQuestionId());
                        if (jq != null) {
                            vo.setQuestionContent(jq.getQuestion());
                        }
                    }
                }
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    public int deleteByExamAndStudent(Integer examCode, Integer studentId) {
        return examAnswerMapper.deleteByExamAndStudent(examCode, studentId);
    }

    @Override
    public int updateAnswer(ExamAnswer answer) {
        return examAnswerMapper.update(answer);
    }

    @Override
    public String getAiAnalysis(Integer answerId, Integer studentId) {
        // 1. 查答题记录
        ExamAnswer answer = examAnswerMapper.findById(answerId);
        if (answer == null) {
            return "⚠️ 未找到该答题记录。";
        }

        // 2. 校验归属
        if (studentId != null && answer.getStudentId() != null
                && !answer.getStudentId().equals(studentId)) {
            return "⚠️ 无权查看该答题记录。";
        }

        // 3. 组装题目详情
        StringBuilder questionInfo = buildQuestionPrompt(answer);

        // 4. 尝试调用 DeepSeek API
        try {
            return callDeepSeekApi(questionInfo.toString(), answer);
        } catch (Exception e) {
            // 降级：返回本地生成的解析
            return generateFallbackAnalysis(answer);
        }
    }

    // ---- AI 相关私有方法 ----

    private StringBuilder buildQuestionPrompt(ExamAnswer answer) {
        StringBuilder sb = new StringBuilder();

        String typeLabel = switch (answer.getQuestionType() != null ? answer.getQuestionType() : 0) {
            case 1 -> "选择题";
            case 2 -> "填空题";
            case 3 -> "判断题";
            default -> "未知类型";
        };
        sb.append("【题目类型】").append(typeLabel).append("\n\n");

        // 查题干
        if (answer.getQuestionType() != null && answer.getQuestionId() != null) {
            switch (answer.getQuestionType()) {
                case 1 -> {
                    MultiQuestion mq = multiQuestionMapper.findById(answer.getQuestionId());
                    if (mq != null) {
                        sb.append("【题目内容】").append(mq.getQuestion()).append("\n");
                        sb.append("A. ").append(mq.getAnswerA() != null ? mq.getAnswerA() : "").append("\n");
                        sb.append("B. ").append(mq.getAnswerB() != null ? mq.getAnswerB() : "").append("\n");
                        sb.append("C. ").append(mq.getAnswerC() != null ? mq.getAnswerC() : "").append("\n");
                        sb.append("D. ").append(mq.getAnswerD() != null ? mq.getAnswerD() : "").append("\n\n");
                    }
                }
                case 2 -> {
                    FillQuestion fq = fillQuestionMapper.findById(answer.getQuestionId());
                    if (fq != null) {
                        sb.append("【题目内容】").append(fq.getQuestion()).append("\n\n");
                    }
                }
                case 3 -> {
                    JudgeQuestion jq = judgeQuestionMapper.findById(answer.getQuestionId());
                    if (jq != null) {
                        sb.append("【题目内容】").append(jq.getQuestion()).append("\n\n");
                    }
                }
            }
        }

        sb.append("【你的答案】").append(answer.getStudentAnswer() != null ? answer.getStudentAnswer() : "未作答").append("\n");
        sb.append("【正确答案】").append(answer.getCorrectAnswer() != null ? answer.getCorrectAnswer() : "暂无").append("\n");

        String resultLabel = answer.getIsCorrect() != null && answer.getIsCorrect() ? "✓ 正确" : "✗ 错误";
        sb.append("【判题结果】").append(resultLabel).append("\n");

        return sb;
    }

    private String callDeepSeekApi(String questionInfo, ExamAnswer answer) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        // 禁用默认的错误处理器，避免 4xx/5xx 直接抛异常，让我们自己解析响应体
        restTemplate.setErrorHandler(new org.springframework.web.client.DefaultResponseErrorHandler() {
            @Override
            public void handleError(java.net.URI url, org.springframework.http.HttpMethod method,
                                    org.springframework.http.client.ClientHttpResponse response) throws java.io.IOException {
                // 不抛异常，让调用方自行判断
            }
        });

        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一位经验丰富的名师，正在为学生讲解一道考试题目。请用亲切、鼓励的语气，给出结构化的考点与思路解析。如果学生答错了，请温和地指出错误原因并给出正确思路；如果答对了，请肯定学生的同时补充更深层的知识点。");

        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", "请针对以下题目给出考点解析：\n\n" + questionInfo + "\n请按以下格式回答：\n📌 考点：<本题考察的核心知识点>\n💡 解题思路：<分步骤讲解解题方法>\n⚠️ 常见误区：<学生容易犯的错误>\n✨ 总结：<一句话要点>");

        Map<String, Object> body = new HashMap<>();
        body.put("model", deepseekModel);
        body.put("messages", List.of(systemMsg, userMsg));
        body.put("temperature", 0.7);
        body.put("max_tokens", 800);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + deepseekApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                deepseekBaseUrl + "/chat/completions", request, String.class);

        // 先解析响应体，提取可能的错误信息
        String responseBody = response.getBody();
        if (responseBody != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            // 检查 API 是否返回了错误信息
            JsonNode errorNode = root.path("error");
            if (!errorNode.isMissingNode()) {
                String errorMsg = errorNode.path("message").asText("未知 API 错误");
                throw new RuntimeException("AI API 错误: " + errorMsg);
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    return choices.get(0).path("message").path("content").asText();
                }
            }
        }

        throw new RuntimeException("API 返回格式异常");
    }

    private String generateFallbackAnalysis(ExamAnswer answer) {
        StringBuilder sb = new StringBuilder();
        sb.append("📌 考点：本题考察对相关知识的理解与运用能力。\n\n");

        boolean correct = answer.getIsCorrect() != null && answer.getIsCorrect();

        if (correct) {
            sb.append("💡 解题思路：你的答案与正确答案一致，说明你已掌握了该知识点的核心概念。建议回顾同类题型的变体，进一步巩固理解。\n\n");
            sb.append("⚠️ 常见误区：部分同学容易在相似概念间混淆，建议注意关键区别。\n\n");
        } else {
            sb.append("💡 解题思路：你的答案 '").append(answer.getStudentAnswer() != null ? answer.getStudentAnswer() : "未作答").append("' 与正确答案 '").append(answer.getCorrectAnswer() != null ? answer.getCorrectAnswer() : "").append("' 存在差异。建议从基本概念出发重新审题，理清题目考察的核心逻辑。\n\n");
            sb.append("⚠️ 常见误区：本题常见的错误包括概念混淆、审题不清或计算疏忽。\n\n");
        }

        sb.append("✨ 总结：多练习同类题型，培养举一反三的能力。\n\n");
        sb.append("（💡 提示：AI 解析服务暂时不可用，以上为本地辅助解析。请稍后重试以获取 AI 智能解析。）");
        return sb.toString();
    }
}
