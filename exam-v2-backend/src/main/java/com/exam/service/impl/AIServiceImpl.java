package com.exam.service.impl;

import com.exam.dto.ChatRequest;
import com.exam.service.AIService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AIServiceImpl implements AIService {

    // ---- 插拔点：将 AI_PROVIDER 改为 "deepseek" 即可切换为真实接口 ----
    private static final String AI_PROVIDER = "mock"; // mock | deepseek

    // DeepSeek 配置（留空，用时填写）
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String DEEPSEEK_API_KEY = ""; // <- 填写你的 API Key

    @Override
    public Map<String, Object> chat(ChatRequest request) {
        if ("deepseek".equals(AI_PROVIDER) && !DEEPSEEK_API_KEY.isEmpty()) {
            return callDeepSeek(request);
        }
        return mockReply(request);
    }

    // ==================== Mock 模式 ====================
    private Map<String, Object> mockReply(ChatRequest request) {
        String userMsg = request.getMessage().toLowerCase().trim();
        String reply = generateMockAnswer(userMsg);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reply", reply);
        result.put("provider", "mock");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    private String generateMockAnswer(String msg) {
        if (msg.contains("成绩") || msg.contains("分数") || msg.contains("考试")) {
            return "成绩查询功能位于左侧菜单「成绩查询」。你可以查看所有历史考试成绩，包括科目、分数和答题日期。每次考试提交后系统会自动判分，选择题和判断题即时出分。";
        }
        if (msg.contains("题库") || msg.contains("题目") || msg.contains("试题")) {
            return "题库管理包含选择题、填空题和判断题三类。教师可以通过左侧菜单「题库管理」进行题目的增删改查。题目按科目和章节分类，方便管理和组卷。";
        }
        if (msg.contains("学生") || msg.contains("管理")) {
            return "学生管理功能允许教师查看、新增、编辑和删除学生信息。学生账号默认密码为123456，首次登录后建议修改。学生信息包含学号、姓名、专业、班级、学院等。";
        }
        if (msg.contains("公告") || msg.contains("通知")) {
            return "公告管理用于发布全校或班级通知。管理员和教师都可以发布公告，学生可以在消息中心查看最新公告和通知。";
        }
        if (msg.contains("你好") || msg.contains("hello") || msg.contains("hi")) {
            return "你好！我是考试系统智能助手，可以帮你了解系统功能。你可以问我关于成绩查询、题库管理、学生管理、公告发布等问题。";
        }
        if (msg.contains("帮助") || msg.contains("功能")) {
            return "本系统主要功能包括：\n1. 考试管理 - 创建和管理在线考试\n2. 题库管理 - 维护选择题、填空题、判断题\n3. 学生管理 - 管理学生信息\n4. 成绩查询 - 查看考试成绩和统计\n5. 公告管理 - 发布系统通知\n6. 在线考试 - 限时答题、自动判分\n你可以问我任何关于这些功能的问题！";
        }
        return "这是一个很好的问题！目前我处于演示模式，可以回答关于系统功能的问题。如需接入真实 AI，请在后端配置 DeepSeek API Key。你可以问我：成绩查询、题库管理、考试流程、学生管理等相关问题。";
    }

    // ==================== DeepSeek 模式 ====================
    private Map<String, Object> callDeepSeek(ChatRequest request) {
        // TODO: 实现 DeepSeek API 调用
        // 1. 构建 messages 数组（含历史记录）
        // 2. POST 到 DEEPSEEK_API_URL
        // 3. 解析响应中的 choices[0].message.content
        // 4. 返回 reply

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reply", "[DeepSeek 模式] API 调用模块待实现。请检查 API Key 配置。");
        result.put("provider", "deepseek");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
