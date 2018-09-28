package com.dc.ws;

import com.dc.f01.common.CommonHelper;
import com.dc.f01.data.*;
import com.dc.f01.interceptors.NoticeMessageHandler;
import com.dc.f01.robot.Robot;
import com.dc.f01.service.IGameService;
import com.dc.f01.service.IWeChatService;
import com.dc.f01.utils.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.TextMessage;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService
public class WxWsServiceImpl implements WxWsService{

	@Autowired
	private IWeChatService iWeChatService;
	@Autowired
	private IGameService gameService;
	@Override
	public void sendWinNo(String gameCode, String period, String winNo,String openType) {
		    /*查询投注记录接口*/
		Message rt = new Message();
		try {
			Map hfAccMap=iWeChatService.getHFaccMsg(); //获取平台账
			String  hfPlayer= hfAccMap.get("HF_ACCOUNTS").toString() ; //PropertiesUtils.findPropertiesKey("hf_domain");
			String  hfPlayerPwd = hfAccMap.get("HF_PWD").toString();  //PropertiesUtils.findPropertiesKey("hfPlayer");
			String  hf_domain=hfAccMap.get("HF_ADDR").toString(); //PropertiesUtils.findPropertiesKey("hfPlayerPwd");
			Calendar cdate = Calendar.getInstance();
			int tagNum=6* cdate.get(Calendar.YEAR);
			String keyStr=tagNum+"huafei";
			String startDate =DateUtil.startDateStr();
			String endDate =DateUtil.endDateStr();
			String data=DataTools.getBase64Encode("{\"$QUERYTYPE\":\"ONCEDATA\",\"$STARTINDEX\":\"1\",\"$ENDINDEX\":\"1000000000\",\"startDate\":\""+startDate+"\",\"endDate\":\""+endDate+"\",\"status\":\"\",\"tranType\":\"\",\"gameType\":\""+gameCode+"\",\"periodNo\":\""+period+"\",\"gameModel\":\"\",\"orderNo\":\"\",\"account\":\"\",\"scope\":\"1\"}");
			String key = MD5Util.Md5(hfPlayer + MD5Util.Md5(hfPlayerPwd) + keyStr);
			String playinfo = hf_domain+"/fxWs/hf/Get_orderList/"+hfPlayer+"/"+hfPlayerPwd+"/"+key+"/"+data;
			String jsonStr_info = "["+HttpUtils.postWsClient(playinfo)+"]";
			JSONObject infoObj = DataTools.stringtoJsonObj(jsonStr_info);
			if(infoObj.get("flag").equals("Y")){
				List  orderList= (List)infoObj.get("gmOdHisList");
				int j =iWeChatService.updateOrderRecord(orderList,gameCode,period,winNo,openType);
				if(j==1){
					//全局推送开奖号码
					Map echoAllMsg= new HashMap();
					echoAllMsg.put("msg",period+"期开奖号码："+winNo);
					echoAllMsg.put("msgType",gameCode);
					echoAllMsg.put("status","2");
					NoticeMessageHandler.sendMessage(new TextMessage(JSONObject.fromObject(echoAllMsg).toString()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void sendUpDownScore( String userid, String type) {
		String msgAllStr="";//全局玩家提示语
		Map echoAllMsg=new HashMap();
		try{
			Map userMap =iWeChatService.getPlayerInfo(userid);
			if("01".equals(type)){
				msgAllStr="玩家["+userMap.get("NICK_NAME")+"]上分已受理，请查收。";
			}else if("02".equals(type)){
				msgAllStr="玩家["+userMap.get("NICK_NAME")+"]下分已受理，请查收。";
			}
			//广播全平台玩家
			echoAllMsg.put("msgType","all");
			echoAllMsg.put("msg", msgAllStr);
			NoticeMessageHandler.sendMessage(new TextMessage(JSONObject.fromObject(echoAllMsg).toString()));
		}catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void updateParamPush() {
		try{
			//参数初始化
			List<PlatformParam> platformParams= null;
			List<BigAccount> bigAccounts= null;
			List<VitrualSet> vitrualSetList = null;
			List<AiRobot> aiList = null;
			List<Game> gameList = null;
			try {
				bigAccounts = gameService.queryBigACCParam();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				platformParams = gameService.queryPlatformParam();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				vitrualSetList = gameService.queryVitrualSetList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				aiList = gameService.queryAIList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				gameList = gameService.queryGameList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//初始个游戏总参数
			if(gameList != null && gameList.size() >0){
				String gameCode="";
				List<VGameParam> vGameParamList = null;//游戏总设置
				List<AiRobot> aiSetList = null;//不同游戏ai设置
				for(int i=0;i < gameList.size();i++){
					gameCode=gameList.get(i).getGameCode();
					try {
						vGameParamList = gameService.queryVgameParamList(gameCode);
						aiSetList    =   gameService.queryGameAIList(gameCode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			//初始化平台参数
			//初始化机器人
			Robot.init(vitrualSetList,aiList,gameList);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
