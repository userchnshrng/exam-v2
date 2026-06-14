package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.dto.ChatRequest;
import com.exam.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ApiResponse<Map<String, Object>> chat(@RequestBody ChatRequest request) {
        Map<String, Object> result = aiService.chat(request);
        return ApiResponse.success(result);
    }
}
