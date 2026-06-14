package com.exam.entity;

/**
 * 公告实体 — 对应 notice 表
 */
public class Notice {

    private Integer noticeId;
    private String content;
    private String createTime;

    public Notice() {
    }

    public Notice(Integer noticeId, String content, String createTime) {
        this.noticeId = noticeId;
        this.content = content;
        this.createTime = createTime;
    }

    public Integer getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Integer noticeId) {
        this.noticeId = noticeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
