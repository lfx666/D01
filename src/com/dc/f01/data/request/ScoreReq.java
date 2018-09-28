package com.dc.f01.data.request;

/**
 * Created by FS-2015036 on 2017/11/4.
 */
public class ScoreReq  extends RequestMsg{
    private String score;
    private String scoreType;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }
}
