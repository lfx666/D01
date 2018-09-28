package com.dc.f01.service;


import com.dc.f01.data.Message;
import com.dc.f01.data.SNSUserInfo;
import com.dc.f01.data.request.PlayReq;
import com.dc.f01.data.request.ScoreReq;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface IWeChatService {


   public Message registerAcc(SNSUserInfo snsUserInfo)throws Exception;

   public Map queryMemberInfo(String userTag)throws Exception;

   public int getOnlineNum()throws Exception;

   public Object getAccountingRecord(String startDate, String endDate, String player, int startIndex, int endIndex, boolean b)throws Exception;

   public Object getOrderRecord(String startDate, String endDate, String player, int startIndex, int endIndex, boolean b)throws Exception;

   public Map getPlayerInfo(String userid)throws Exception;

   public String insertOrderRecord(String orderNo, PlayReq playReq,String userId)throws Exception;

   public Map getGameParam(String gmID,String userId)throws Exception;

   public String upDownScore(String userId, ScoreReq req)throws Exception;

   public Map getHFaccMsg()throws Exception;

   public Message login(SNSUserInfo snsUserInfo, String state,String ipAddr,String userId)throws Exception;

   public int updateOrderRecord(List orderObj,String gameCode,String period,String winNo,String openType)throws Exception;

   public int insertAwardPeriod(String game_id, String enter_period)throws Exception;

   public Message accountLogin(String account, String pwd,String ipAddr)throws Exception;

   public Message bindAccount(SNSUserInfo snsUserInfo, String account, String pwd)throws Exception;

   public Map queryGameInfo(String gameId)throws Exception;

   public String queryMemberOpenIdByAcc(String account)throws Exception;

   public String getPlatName()throws Exception;

   Long getGameDelayTime(String gameCode)throws Exception;
}
