package com.exam.entity;

/**
 * 回复 — 对应 replay 表
 */
public class Replay {

    private Integer messageId;
    private Integer replayId;
    private String replay;
    private String replayTime;

    public Replay() {
    }

    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }
    public Integer getReplayId() { return replayId; }
    public void setReplayId(Integer replayId) { this.replayId = replayId; }
    public String getReplay() { return replay; }
    public void setReplay(String replay) { this.replay = replay; }
    public String getReplayTime() { return replayTime; }
    public void setReplayTime(String replayTime) { this.replayTime = replayTime; }
}
