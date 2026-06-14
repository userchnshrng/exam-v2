package com.exam.service;

import com.exam.dto.ChatRequest;
import java.util.Map;

public interface AIService {

    /** 发送消息并获取 AI 回复 */
    Map<String, Object> chat(ChatRequest request);
}
