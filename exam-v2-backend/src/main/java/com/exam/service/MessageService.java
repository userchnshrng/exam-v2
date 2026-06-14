package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.Message;
import com.exam.entity.Replay;

import java.util.List;

public interface MessageService {

    PageResult<Message> listMessages(int page, int size);

    Message getMessage(Integer id);

    Message createMessage(Message message);

    List<Replay> getReplies(Integer messageId);

    Replay createReply(Replay replay);
}
