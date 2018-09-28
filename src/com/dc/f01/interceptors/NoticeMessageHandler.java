package com.dc.f01.interceptors;

import java.util.*;

import com.dc.f01.common.CommonHelper;
import com.dc.f01.data.PlatformParam;
import com.dc.f01.data.request.*;
import com.dc.f01.service.IWeChatService;
import com.dc.f01.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dc.f01.common.CodeConstant;
import com.dc.f01.common.MsgConstants;
import com.dc.f01.task.DxQueuePool;
import com.dc.f01.task.QueueFactory;

import net.sf.json.JSONObject;
/**
 * 
 * @author Administrator
 * @see
 * //摇骰子的时候发现websocket断了重新连接在传输
 */
@Component
public class NoticeMessageHandler implements WebSocketHandler {

	private static transient Log log = LogFactory.getLog(NoticeMessageHandler.class);

//	@Autowired
//	private IGameService gameService;
	
	
//	@Autowired
//	private ILoginService loginService;

	@Autowired
	private IWeChatService iWeChatService;
  
   private static DxQueuePool<String> pool;
    
   static{
    	pool = QueueFactory.create();
    	pool.schedule();
    }
	
    //存储当前所有的在线用户socket
    //目前以userId为key,所以一个用户打开多个socket页面时只会在最新的页面推送消息
    private static final Map<Long, WebSocketSession> users = Collections.synchronizedMap(new HashMap<Long, WebSocketSession>());

    
    public static Map<Long, WebSocketSession> getOnline(){
    	return users;
    }
    
    public static DxQueuePool<String> getPool(){
    	return pool;
    }

    //socket 连接常见时该方法被调用
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
    	//在socket Interceptor中设置的参数
        Object userIdObj = webSocketSession.getAttributes().get(CodeConstant.SOCKET_USER);
        if(userIdObj == null){
        	return;
        }
        if(NumbericTools.obj2Long(userIdObj).longValue() == 0l){
        	return;
        }
        users.put(NumbericTools.obj2Long(userIdObj), webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
    	//前端发送消息时调用该方法
    	synchronized (webSocketSession) {
    		//MSG消息
    		Object obj = webSocketMessage.getPayload();
    		//空请求不做任何处理
    		if(obj == null){
    			return;
    		}
    		log.info("#####payLoad: "+ StringTools.obj2String(obj));
    		JSONObject jo = JSONObject.fromObject(obj);
    		String msgType = jo.getString("msgType");
			JSONObject jsonObject=JSONObject.fromObject(jo.getString("msg"));
    		if(StringTools.isBlankNull(msgType)){
    			if(webSocketSession.isOpen()){
    				webSocketSession.sendMessage(new TextMessage("-1"));//消息代码不存在]
    			}
    			return;
    		}
    		RequestMsg msg = null;
    		switch(msgType){
    			case MsgConstants.LOGIN: {
    				msg = (LoginReq)JSONObject.toBean(jsonObject,LoginReq.class);
					break;
    			}
    			case MsgConstants.PLAY: {
    				msg = (PlayReq)JSONObject.toBean(jsonObject,PlayReq.class);
					break;
    			}
				case MsgConstants.SCORE: {
					msg = (ScoreReq)JSONObject.toBean(jsonObject,ScoreReq.class);
					break;
				}
    			case MsgConstants.SHARE: {
    				msg = (ShareReq)JSONObject.toBean(jo,ShareReq.class);
					break;
    			}
    		}
    		//====================================================================
    		log.info("[class:NoticeMessageHandler  method:handleMessage] userId: "+ webSocketSession.getAttributes().get(CodeConstant.SOCKET_USER));
    		
    		//登录
    		if(MsgConstants.LOGIN.equals(msgType)){
    			LoginReq req = (LoginReq) msg;
    			Map userMsg=iWeChatService.getPlayerInfo(req.getUserId().toString());
    			//用户ID存放在session
    			webSocketSession.getAttributes().put(CodeConstant.SOCKET_USER, req.getUserId());
				webSocketSession.getAttributes().put(CodeConstant.USER_MSG, userMsg);
				//平台参数
				List platFormParam =null;
				PlatformParam platFormMap=(PlatformParam)platFormParam.get(0);
				userMsg.put("CS_IMG",platFormMap.getCsImg());
				userMsg.put("PLAT_LOGO",platFormMap.getPlatLogo());
				userMsg.put("ADMIN_NAME",platFormMap.getAdminName());
				userMsg.put("ADMIN_IMG",platFormMap.getAdminImg());
				userMsg.put("PLAT_NAME",platFormMap.getPlatName());
				userMsg.put("CS_QR_CODE",platFormMap.getCsQrCode());
				userMsg.put("FINANCE_QR_CODE",platFormMap.getFinanceQrCode());
				userMsg.put("CS_NAME",platFormMap.getCsName());
				users.put(req.getUserId(), webSocketSession);

				//获取总余额
				Map hfAccMap=iWeChatService.getHFaccMsg(); //获取平台账
				String  hfPlayer= hfAccMap.get("HF_ACCOUNTS").toString() ; //PropertiesUtils.findPropertiesKey("hf_domain");
				String  hfPlayerPwd = hfAccMap.get("HF_PWD").toString();  //PropertiesUtils.findPropertiesKey("hfPlayer");
				String  hf_domain =hfAccMap.get("HF_ADDR").toString(); //PropertiesUtils.findPropertiesKey("hfPlayerPwd");
				Calendar cdate = Calendar.getInstance();
				int tagNum=6* cdate.get(Calendar.YEAR);
				String keyStr=tagNum+"huafei";
				String key = MD5Util.Md5(hfPlayer+MD5Util.Md5(hfPlayerPwd)+keyStr);
				String playinfo = hf_domain+"/fxWs/hf/Get_Balance/"+hfPlayer+"/"+hfPlayerPwd+"/"+key+"/";
				String jsonStr_info = "["+HttpUtils.postWsClient(playinfo)+"]";
				JSONObject infoObj = StringTools.stringtoJsonObj(jsonStr_info);
				if("Y".equals(infoObj.get("flag"))) {
					//那个账号余额
					userMsg.put("BOSSCOINS",infoObj.get("balance"));
				}
				userMsg.put("msgType",msgType);
    			String jsonStr = JSONObject.fromObject(userMsg).toString();
            	sendMessage(new TextMessage(jsonStr),req.getUserId());
            	return;
    		}
    		//下注
    		else if(MsgConstants.PLAY.equals(msgType)){
    			Long userId= NumbericTools.obj2Long(webSocketSession.getAttributes().get(CodeConstant.SOCKET_USER));
				Map userMap= (Map) webSocketSession.getAttributes().get(CodeConstant.USER_MSG);
				    Map echoMsg=new HashMap();
				    PlayReq playReq = (PlayReq) msg;
					Map gameParam = iWeChatService.getGameParam(playReq.getGmId(),DataUtil.getAsString(userId));
					Map limitParam =(Map)gameParam.get("BET_LIMIT");
					String gameSwitch=limitParam.get("IS_OPEN").toString();
				    Double  maxLimit =DataUtil.getAsDouble(limitParam.get("MAX_BET"));
				    Double  minLimit =DataUtil.getAsDouble(limitParam.get("MIN_BET"));
				    Double userCoins= DataUtil.getAsDouble(gameParam.get("USER_COINS"));
					if("0".equals(gameSwitch)){
						echoMsg.put("msgType","play");
						echoMsg.put("status","-3");
						echoMsg.put("msg", "投注失败。");
						sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),userId);
						return;
					}
					if(userCoins<DataUtil.getAsDouble(playReq.getByt1TTMoney())){
						echoMsg.put("msgType","play");
						echoMsg.put("status","-2");
						echoMsg.put("msg", "您的余额不足，竞猜失败，请上分/充值。");
						sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),userId);
						return;
					}
					if(DataUtil.getAsDouble(playReq.getByt1TTMoney())<minLimit){
						echoMsg.put("msgType","play");
						echoMsg.put("status","-1");
						echoMsg.put("msg", "低于竞猜限额，竞猜失败。");
						sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),userId);
						return;
					}
					if(DataUtil.getAsDouble(playReq.getByt1TTMoney())>maxLimit){
						echoMsg.put("msgType","play");
						echoMsg.put("status","-1");
						echoMsg.put("msg", "高于竞猜限额，竞猜失败。");
						sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),userId);
						return;
					}
					Map hfAccMap=iWeChatService.getHFaccMsg(); //获取平台账
					String  hfPlayer= hfAccMap.get("HF_ACCOUNTS").toString() ; //PropertiesUtils.findPropertiesKey("hf_domain");
					String  hfPlayerPwd = hfAccMap.get("HF_PWD").toString();  //PropertiesUtils.findPropertiesKey("hfPlayer");
					String  hf_domain=hfAccMap.get("HF_ADDR").toString(); //PropertiesUtils.findPropertiesKey("hfPlayerPwd");
					Calendar cdate = Calendar.getInstance();
					int tagNum=6* cdate.get(Calendar.YEAR);
					String keyStr=tagNum+"huafei";
					String key = MD5Util.Md5(hfPlayer + MD5Util.Md5(hfPlayerPwd) + keyStr);
					String data= DataUtil.getBase64Encode("{\"GP_CODE\":\"" + playReq.getGpCode() + "\"," +
							"\"GM_ID\":\"" + playReq.getGmId() + "\"," +
							"\"BET_M\":\"" + playReq.getBetM() + "\"," +
							"\"BUY_TP\":\"" + playReq.getBuyType() + "\"," +
							"\"BYT1_CUR_PNO\":\"" + playReq.getByt1CurPno() + "\"," +
							"\"BYT1_TT_NUM\":\"" + playReq.getByt1TTNum() + "\"," +
							"\"BYT1_TT_MONEY\":\"" + playReq.getByt1TTMoney() + "\"," +
							"\"BT_DTLS\":\"" + playReq.getBetDetails() + "\"}");
					String playinfo = hf_domain+"/fxWs/hf/Create_Orders/"+hfPlayer+"/"+hfPlayerPwd+"/"+key+"/"+data;
					String jsonStr_info = "["+HttpUtils.postWsClient(playinfo)+"]";
					JSONObject infoObj = DataUtil.stringtoJsonObj(jsonStr_info);
					if("Y".equals(infoObj.get("flag"))) {
						String succMsg="";
						if(playReq.getPlay().equals("DXDS")){
							 succMsg="期号："+playReq.getByt1CurPno()+"，第"+playReq.getPos()+"位，号码："+playReq.getBuyNum()+"，单注："+playReq.getByt1TTMoney()+"，总金额："+playReq.getByt1TTMoney()+"，竞猜成功。";
						}else if(playReq.getPlay().equals("DWD")){
							 succMsg="期号："+playReq.getByt1CurPno()+"，第"+playReq.getPos()+"位，号码："+playReq.getBuyNum()+"，单注："+playReq.getByt1TTMoney()+"，总金额："+DataUtil.getAsInt(playReq.getByt1TTMoney())*DataUtil.getAsInt(playReq.getByt1TTNum())+"，竞猜成功。";
						}
						echoMsg.put("msg",succMsg);
						echoMsg.put("status","1");
						echoMsg.put("msgType","play");
						String orderNo=infoObj.get("orderno").toString();
						//投注成功插入数据/扣掉金额/返回投注后的金额
						String coins=iWeChatService.insertOrderRecord(orderNo,playReq,DataUtil.getAsString(userId));
						echoMsg.put("coins",coins);
						Map echoAllMsg = new HashMap();
						echoAllMsg.put("msg",playReq.getFrontBetContent()+"/"+playReq.getByt1TTMoney());
						echoAllMsg.put("msgType","player");
						echoAllMsg.put("nickName", userMap.get("nick_name"));
						echoAllMsg.put("headImg", userMap.get("head_img"));
						//全局发送玩家投注信息
						sendMessage(new TextMessage(JSONObject.fromObject(echoAllMsg).toString()));
						//客服一对一回复
						sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),userId );


						return;
					}else{
						echoMsg.put("msgType","play");
						echoMsg.put("status","-1");
						if(infoObj.get("msg").toString().equals("null")){
							echoMsg.put("msg", "系统异常,请刷新");
						}else{
							echoMsg.put("msg", infoObj.get("msg").toString());
						}
						//向单个用户发；
						sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),userId);
						return;
					}
    		}
			//上下分申请
			else if(MsgConstants.SCORE.equals(msgType)){
				ScoreReq req = (ScoreReq) msg;
				Long userId= NumbericTools.obj2Long(webSocketSession.getAttributes().get(CodeConstant.SOCKET_USER));
				Map echoMsg=new HashMap();
				Map echoAllMsg=new HashMap();
				if(userId == null){
					echoMsg.put("msgType","score");
					echoMsg.put("msg", "错误信息：用户信息获取失败");
					sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()), userId);
					return;
				}
				String resultStr=iWeChatService.upDownScore(DataUtil.getAsString(userId),req);
				String resultArr[]=resultStr.split("`");
				String msgStr=""; //对应玩家提示语
//				String msgAllStr="";//全局玩家提示语
				if("UP".equals(req.getScoreType())){
					if("1".equals(resultArr[0])){
						msgStr="上/"+req.getScore()+"&nbsp;&nbsp;上分已记录，单号："+resultArr[1]+"，转账请联系客服，获取最新收款方式。";
//						msgAllStr="玩家["+req.getNickname()+"]上分已受理";
						echoMsg.put("status","1");
						echoMsg.put("coins",NumbericTools.getFormCoinVal(resultArr[2]));
					}else{
						echoMsg.put("status","-2");
						msgStr="系统异常，转账请联系客服，获取最新收款方式。";
					}
				}else if("DOWN".equals(req.getScoreType())){
					if("1".equals(resultArr[0])){
						 msgStr="下/"+req.getScore()+"&nbsp;&nbsp;下分已记录，单号："+resultArr[1]+",转账请联系客服，,提供您的收(付)款方式。";
//						 msgAllStr="玩家["+req.getNickname()+"]下分已受理";
						 echoMsg.put("status","1");
						 echoMsg.put("coins",NumbericTools.getFormCoinVal(resultArr[2]));
					}else if("-2".equals(resultArr[0])){
						msgStr="下分失败，错误：剩余积分不足!!";
						echoMsg.put("status","-1");
					}else{
						echoMsg.put("status","-2");
						msgStr="系统异常，转账请联系客服，获取最新付款方式。";
					}
				}
				echoMsg.put("msgType","score");
				echoMsg.put("msg", msgStr);
				sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()), userId);
				//广播全平台玩家
//				echoAllMsg.put("msgType","all");
//				echoAllMsg.put("msg", msgAllStr);
//				sendMessage(new TextMessage(JSONObject.fromObject(echoAllMsg).toString()));
				return;
			}
		}
    	
    }

	//连接出错时
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        /*if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }*/
        Long userId = NumbericTools.obj2Long(webSocketSession.getAttributes().get("SOCKET_USER"));
        users.remove(userId);
    }

    //连接关闭时
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        Long userId = NumbericTools.obj2Long(webSocketSession.getAttributes().get("SOCKET_USER"));
        users.remove(userId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //向某个用户发送消息
    public static void sendMessage(TextMessage message, Long userId) {
        WebSocketSession user = users.get(userId);
        synchronized(user) {
	        if (null != user && user.isOpen()) {
	            try {
	                user.sendMessage(message);
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
        }
    }

    //批量向所有用户发送消息
    public static void sendMessage(TextMessage message) {
    	if(users == null || users.size() <= 0){
    		return;
    	}
    	Collection<WebSocketSession> collection = users.values();
    	if(collection != null && !collection.isEmpty()){
	        for (WebSocketSession user : collection) {
	        	synchronized(user) {
		            if (user.isOpen()) {
		                try {
		                    user.sendMessage(message);
		                } catch (Exception e) {
		                	
		                }
		            }
	        	}
	        }
    	}
    } 
    
    
 
    
}