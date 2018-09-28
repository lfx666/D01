package com.dc.ws;

import javax.jws.WebService;

@WebService
public interface WxWsService {
	//华菲后台推送使用
	public void sendWinNo(String gameCode,String period,String winNo,String openType);
	//华菲微信后台推送使用
	public void sendUpDownScore(String userid,String score);
	//华菲微信后台推送使用
	public void updateParamPush();
}
