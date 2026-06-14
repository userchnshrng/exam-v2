package com.exam.service;

import com.exam.common.PageResult;
import com.exam.entity.Notice;

public interface NoticeService {

    PageResult<Notice> list(String keyword, int page, int size);

    Notice getById(Integer noticeId);

    Notice create(Notice notice);

    Notice update(Notice notice);

    void delete(Integer noticeId);
}
