package com.dc.f01.data.request;

public class PlayReq extends RequestMsg{
    private String gpCode;
    private String gmId;
    private String betM;
    private String buyType;
    private String byt1CurPno;
    private String byt1TTNum;
    private String byt1TTMoney;
    private String betDetails;
    private String play;
    private String betContent;
    private String pos;
    private String buyNum;
    private String frontBetContent;

    public String getFrontBetContent() {
        return frontBetContent;
    }

    public void setFrontBetContent(String frontBetContent) {
        this.frontBetContent = frontBetContent;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(String buyNum) {
        this.buyNum = buyNum;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public String getBetContent() {
        return betContent;
    }

    public void setBetContent(String betContent) {
        this.betContent = betContent;
    }

    public String getGmId() {
        return gmId;
    }

    public void setGmId(String gmId) {
        this.gmId = gmId;
    }

    public String getGpCode() {
        return gpCode;
    }

    public void setGpCode(String gpCode) {
        this.gpCode = gpCode;
    }

    public String getBetM() {
        return betM;
    }

    public void setBetM(String betM) {
        this.betM = betM;
    }

    public String getBuyType() {
        return buyType;
    }

    public void setBuyType(String buyType) {
        this.buyType = buyType;
    }

    public String getByt1CurPno() {
        return byt1CurPno;
    }

    public void setByt1CurPno(String byt1CurPno) {
        this.byt1CurPno = byt1CurPno;
    }

    public String getByt1TTNum() {
        return byt1TTNum;
    }

    public void setByt1TTNum(String byt1TTNum) {
        this.byt1TTNum = byt1TTNum;
    }

    public String getByt1TTMoney() {
        return byt1TTMoney;
    }

    public void setByt1TTMoney(String byt1TTMoney) {
        this.byt1TTMoney = byt1TTMoney;
    }

    public String getBetDetails() {
        return betDetails;
    }

    public void setBetDetails(String betDetails) {
        this.betDetails = betDetails;
    }
}
