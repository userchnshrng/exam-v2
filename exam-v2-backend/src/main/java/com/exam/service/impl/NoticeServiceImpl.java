package com.exam.service.impl;

import com.exam.common.PageResult;
import com.exam.entity.Notice;
import com.exam.mapper.NoticeMapper;
import com.exam.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public PageResult<Notice> list(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Notice> records = noticeMapper.list(keyword, offset, size);
        long total = noticeMapper.count(keyword);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public Notice getById(Integer noticeId) {
        Notice notice = noticeMapper.findById(noticeId);
        if (notice == null) {
            throw new RuntimeException("公告不存在: " + noticeId);
        }
        return notice;
    }

    @Override
    public Notice create(Notice notice) {
        noticeMapper.insert(notice);
        return notice;
    }

    @Override
    public Notice update(Notice notice) {
        Notice existing = getById(notice.getNoticeId());
        noticeMapper.update(notice);
        return notice;
    }

    @Override
    public void delete(Integer noticeId) {
        getById(noticeId);
        noticeMapper.delete(noticeId);
    }
}
