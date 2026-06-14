package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.Message;
import com.exam.entity.Replay;
import com.exam.mapper.MessageMapper;
import com.exam.mapper.ReplayMapper;
import com.exam.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final ReplayMapper replayMapper;

    public MessageServiceImpl(MessageMapper messageMapper, ReplayMapper replayMapper) {
        this.messageMapper = messageMapper;
        this.replayMapper = replayMapper;
    }

    @Override
    public PageResult<Message> listMessages(int page, int size) {
        int offset = (page - 1) * size;
        List<Message> records = messageMapper.list(offset, size);
        return new PageResult<>(records, messageMapper.count(), page, size);
    }

    @Override
    public Message getMessage(Integer id) {
        Message message = messageMapper.findById(id);
        if (message == null) throw new RuntimeException("留言不存在: " + id);
        return message;
    }

    @Override
    public Message createMessage(Message message) {
        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<Replay> getReplies(Integer messageId) {
        return replayMapper.findByMessageId(messageId);
    }

    @Override
    public Replay createReply(Replay replay) {
        replayMapper.insert(replay);
        return replay;
    }
}
