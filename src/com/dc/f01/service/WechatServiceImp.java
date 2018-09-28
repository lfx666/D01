package com.dc.f01.service;


import com.dc.f01.common.AppException;
import com.dc.f01.common.CodeConstant;
import com.dc.f01.dao.IWeChatDao;
import com.dc.f01.data.Message;
import com.dc.f01.data.SNSUserInfo;
import com.dc.f01.data.request.PlayReq;
import com.dc.f01.data.request.ScoreReq;
import com.dc.f01.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("iWeChatService")
public class WechatServiceImp implements IWeChatService {
	@Autowired
	private IWeChatDao iWeChatDao;
	@Override
	public Message registerAcc(SNSUserInfo snsUserInfo)throws Exception {
		Message msg = new Message();
		try{
			String resultCode = iWeChatDao.registerAcc(snsUserInfo);
			msg.setResultCode(resultCode);
			return msg;
		}catch(Exception ex){
			ex.printStackTrace();
			throw new AppException(CodeConstant.E0001);
		}
	}

	@Override
	public Map queryMemberInfo(String userTag) throws Exception {
		return iWeChatDao.queryMemberInfo(userTag);
	}

	@Override
	public int getOnlineNum() throws Exception {
		return iWeChatDao.getOnlineNum();
	}

	@Override
	public Object getAccountingRecord(String startDate, String endDate, String player, int startIndex, int endIndex, boolean b)throws Exception {
		return iWeChatDao.getAccountingRecord( startDate,  endDate,  player,  startIndex,  endIndex,  b);
	}

	@Override
	public Object getOrderRecord(String startDate, String endDate, String player, int startIndex, int endIndex, boolean b) throws Exception {
		return iWeChatDao.getOrderRecord(startDate, endDate, player, startIndex, endIndex, b);
	}

	@Override
	public Map getPlayerInfo(String userid) throws Exception {
		return iWeChatDao.getPlayerInfo(userid);
	}

	@Override
	public String insertOrderRecord(String orderNo, PlayReq playReq,String userId) throws Exception {
		return iWeChatDao.insertOrderRecord(orderNo,playReq,userId);
	}

	@Override
	public Map getGameParam(String gmID,String userId) throws Exception {
		return iWeChatDao.getGameParam( gmID, userId);
	}

	@Override
	public String upDownScore(String userId, ScoreReq req) throws Exception {
		return iWeChatDao.upDownScore(userId, req);
	}

	@Override
	public Map getHFaccMsg() throws Exception {
		return iWeChatDao.getHFaccMsg();
	}

	@Override
	public Message login(SNSUserInfo snsUserInfo, String state,String ipAddr,String userId) throws Exception {
		Message msg = new Message();
		try{
			String resultCode = iWeChatDao.login(snsUserInfo,state,ipAddr, userId);
			if ("1".equals(resultCode)){
				msg.setStatus(true);
				msg.setResultCode(resultCode);
				msg.setDescription("登入成功");
			}else if("2".equals(resultCode)){
				msg.setStatus(false);
				msg.setResultCode(resultCode);
				msg.setDescription("玩家不存在");
			}else if("4".equals(resultCode)){
				msg.setStatus(false);
				msg.setResultCode(resultCode);
				msg.setDescription("您的账号已被禁用");
			}else{
				msg.setStatus(false);
				msg.setResultCode(resultCode);
				msg.setDescription("系统异常");
			}
			return msg;
		}catch(Exception ex){
			ex.printStackTrace();
			throw new AppException(CodeConstant.E0001);
		}
	}

	@Override
	public int updateOrderRecord(List orderObj,String gameCode,String period,String winNo,String openType) throws Exception {
		return iWeChatDao.updateOrderRecord(orderObj, gameCode, period, winNo, openType);
	}

	@Override
	public int insertAwardPeriod(String game_id, String enter_period) throws Exception {
		return iWeChatDao.insertAwardPeriod( game_id,  enter_period);
	}

	@Override
	public Message accountLogin(String account, String pwd,String ipAddr) throws Exception {
		Message msg = new Message();
		msg.setStatus(true);
		try{
			String resultCode = iWeChatDao.accountLogin(account, pwd, ipAddr, "mobielWeb");
			if ("1".equals(resultCode)){
				Map map =iWeChatDao.getHFaccMsg();
				msg.put("appid",map.get("APP_ID").toString());
				msg.put("addr",map.get("ADDR").toString());
				msg.setResultCode(resultCode);
				msg.setDescription("登入成功");
			}else if("2".equals(resultCode)){
				Map map =iWeChatDao.getHFaccMsg();
				msg.put("appid",map.get("APP_ID").toString());
				msg.put("addr",map.get("ADDR").toString());
				msg.setResultCode(resultCode);
				msg.setDescription("玩家不存在");
			}else if("4".equals(resultCode)){
				msg.setResultCode(resultCode);
				msg.setDescription("您的账号已被禁用");
			}else if("6".equals(resultCode)){
				Map  playerMap = iWeChatDao.getPlayerInfoByAcc(account);
				if(playerMap.get("LOGIN_FAIL_NUM")=="5"){
					msg.setDescription("对不起，您已被登录禁用");
				}else{
					msg.setDescription("密码输入错误,登录剩余次数:"+(5- DataTools.getAsInt(playerMap.get("LOGIN_FAIL_NUM")))+"次");
				}
			}else{
				msg.setResultCode(resultCode);
				msg.setDescription("系统异常");
			}
			return msg;
		}catch(Exception ex){
			ex.printStackTrace();
			throw new AppException(CodeConstant.E0001);
		}
	}

	@Override
	public Message bindAccount(SNSUserInfo snsUserInfo, String account, String pwd) throws Exception {
		Message msg = new Message();
		msg.setStatus(true);
		try{
			String resultCode = iWeChatDao.bindAccount(snsUserInfo, MD5Util.Md5(pwd),account);
			if ("1".equals(resultCode)){
				msg.setResultCode(resultCode);
				msg.setDescription("绑定成功");
			}else if("0".equals(resultCode)){
				msg.setResultCode(resultCode);
				msg.setDescription("您已绑定过账号");
			}else if("-1".equals(resultCode)){
				msg.setResultCode(resultCode);
				msg.setDescription("已存在相同的会员名，请换个会员名");
			}else{
				msg.setResultCode(resultCode);
				msg.setDescription("系统异常");
			}
			return msg;
		}catch(Exception ex){
			ex.printStackTrace();
			throw new AppException(CodeConstant.E0001);
		}
	}

	@Override
	public Map queryGameInfo(String gameId) throws Exception {
		return iWeChatDao.queryGameInfo(gameId);
	}

	@Override
	public String queryMemberOpenIdByAcc(String account) throws Exception {
		return iWeChatDao.queryMemberOpenIdByAcc(account);
	}

	@Override
	public String getPlatName() throws Exception {
		return iWeChatDao.getPlatName();
	}

	@Override
	public Long getGameDelayTime(String gameCode) throws Exception {
		return iWeChatDao.getGameDelayTime(gameCode);
	}


}
