package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.common.PageResult;
import com.exam.entity.Message;
import com.exam.entity.Replay;
import com.exam.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResponse<PageResult<Message>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(messageService.listMessages(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Message> getById(@PathVariable("id") Integer id) {
        return ApiResponse.success(messageService.getMessage(id));
    }

    @PostMapping
    public ApiResponse<Message> create(@RequestBody Message message) {
        return ApiResponse.success(messageService.createMessage(message));
    }

    @GetMapping("/{id}/replies")
    public ApiResponse<List<Replay>> getReplies(@PathVariable("id") Integer id) {
        return ApiResponse.success(messageService.getReplies(id));
    }

    @PostMapping("/{id}/replies")
    public ApiResponse<Replay> createReply(@PathVariable("id") Integer id, @RequestBody Replay replay) {
        replay.setMessageId(id);
        return ApiResponse.success(messageService.createReply(replay));
    }
}
