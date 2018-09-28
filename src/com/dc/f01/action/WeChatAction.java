package com.dc.f01.action;

import com.dc.f01.common.CommonHelper;
import com.dc.f01.common.CommonUtil;
import com.dc.f01.data.PlatformParam;
import com.dc.f01.data.SNSUserInfo;
import com.dc.f01.data.WeixinOauth2Token;
import com.dc.f01.data.Message;
import com.dc.f01.service.IWeChatService;
import com.dc.f01.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.*;

@Controller
public class WeChatAction extends BaseAction {
    protected static transient Log log = LogFactory.getLog(WeChatAction.class);
    public static final String TOKEN = "weixin";
//    private static Map wxMap=new HashMap();

    @Autowired
    private IWeChatService iWeChatService;
    /*微信验证token

     */
    @RequestMapping("/wechat/virifyToken.do")
    public void virifyToken(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            // 开发者提交信息后，微信服务器将发送GET请求到填写的服务器地址URL上，GET请求携带参数
            String signature = request.getParameter("signature");// 微信加密签名（token、timestamp、nonce。）
            String timestamp = request.getParameter("timestamp");// 时间戳
            String nonce = request.getParameter("nonce");// 随机数
            String echostr = request.getParameter("echostr");// 随机字符串
            PrintWriter out = response.getWriter();
            // 将token、timestamp、nonce三个参数进行字典序排序
            String[] params = new String[] { TOKEN, timestamp, nonce };
            Arrays.sort(params);
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            String clearText = params[0] + params[1] + params[2];
            String algorithm = "SHA-1";
            String sign = new String(
                    Hex.encodeHex(MessageDigest.getInstance(algorithm).digest((clearText).getBytes()), true));
            // 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
            if (signature.equals(sign)) {
                response.getWriter().print(echostr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*获取微信授权
     */
    @RequestMapping("/wechat/weChatOAuth.do")
    public void getWeChatCode(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            // 用户同意授权后，能获取到code
            String code = request.getParameter("code");
            String state = request.getParameter("state");
            // 用户同意授权
            if (!"authdeny".equals(code)) {
                // 获取网页授权access_token
                Map WechatInfoMap=iWeChatService.getHFaccMsg();
                WeixinOauth2Token weixinOauth2Token = getOauth2AccessToken(WechatInfoMap.get("APP_ID").toString() ,WechatInfoMap.get("PWD").toString(), code);
                if(weixinOauth2Token!=null){
                    // 网页授权接口访问凭证
                    String accessToken = weixinOauth2Token.getAccessToken();
                    // 用户标识
                    String openId = weixinOauth2Token.getOpenId();
                    // 获取用户信息
                    SNSUserInfo snsUserInfo = getSNSUserInfo(accessToken, openId);
                    //微信直接登入
                    if("weixin".equals(state)){
                    //注册账号进入大厅
                        rt =iWeChatService.registerAcc(snsUserInfo);
                        //1:注册成功 2：玩家已存在
                        if("1".equals(rt.getResultCode())||"2".equals(rt.getResultCode())){
                            Map mp = new HashMap();
                            String ipAddr = HttpUtils.getRequestIp(request);
                            mp=iWeChatService.queryMemberInfo(snsUserInfo.getOpenId().toString());
                            String userId=mp.get("USER_ID").toString();
                            rt=iWeChatService.login(snsUserInfo,state,ipAddr,userId);//玩家登入
                            rt.setStatus(true);
                            if("1".equals(rt.getResultCode())){
                                rt.addMap("MEM_INFO",mp);
                                rt.put("ADDR",WechatInfoMap.get("ADDR"));
                            }
                            JsonTools.outPutJson(rt.getData(), response);
                        }
                    }
                }
            }else{
                Map WechatInfoMap=iWeChatService.getHFaccMsg();
                if("accLogin".equals(state)){
                    Map mp = new HashMap();
                    String ipAddr = HttpUtils.getRequestIp(request);
                    String account = request.getParameter("account");
                    String openId= iWeChatService.queryMemberOpenIdByAcc(account);
                    mp=iWeChatService.queryMemberInfo(openId);
                    String userId=mp.get("USER_ID").toString();
                    SNSUserInfo snsUserInfo = new SNSUserInfo();
                    snsUserInfo.setOpenId(openId);
                    rt=iWeChatService.login(snsUserInfo,state,ipAddr,userId);//玩家登入
                    rt.setStatus(true);
                    if("1".equals(rt.getResultCode())){
                        rt.addMap("MEM_INFO",mp);
                        rt.put("ADDR",WechatInfoMap.get("ADDR"));
                    }
                    JsonTools.outPutJson(rt.getData(), response);

                } else if("bindAcc".equals(state)){
                    String account = request.getParameter("account");
                    String pwd = request.getParameter("pwd");
                    String openID = request.getParameter("userTag");
                    SNSUserInfo snsUserInfo = new SNSUserInfo();
                    snsUserInfo.setOpenId(openID);
                    rt.setStatus(true);
                    rt=iWeChatService.bindAccount(snsUserInfo, account, pwd);//绑定账号密码
                    JsonTools.outPutJson(rt.getData(), response);

                }else if("fresh".equals(state)){
                    Map mp = new HashMap();
                    String userId = request.getParameter("userId");
                     mp= iWeChatService.getPlayerInfo(userId);
                    rt.setStatus(true);
                    rt.setResultCode("1");
                    rt.addMap("MEM_INFO",mp);
                    rt.put("ADDR", WechatInfoMap.get("ADDR"));
                    JsonTools.outPutJson(rt.getData(), response);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取网页授权凭证
     *
     * @param appId 公众账号的唯一标识
     * @param appSecret 公众账号的密钥
     * @param code
     * @return WeixinAouth2Token
     */
    public static WeixinOauth2Token getOauth2AccessToken(String appId, String appSecret, String code) {
        WeixinOauth2Token wat = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        requestUrl = requestUrl.replace("APPID", appId);
        requestUrl = requestUrl.replace("SECRET", appSecret);
        requestUrl = requestUrl.replace("CODE", code);
        // 获取网页授权凭证
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        if (null != jsonObject) {
            try {
                wat = new WeixinOauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getInt("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            } catch (Exception e) {
                wat = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:"+errorCode+" errmsg:"+errorMsg);
            }
        }
        return wat;
    }
    /**
     * 通过网页授权获取用户信息
     *
     * @param accessToken 网页授权接口调用凭证
     * @param openId 用户标识
     * @return SNSUserInfo
     */
    @SuppressWarnings( { "deprecation", "unchecked" })
    public static SNSUserInfo getSNSUserInfo(String accessToken, String openId) {
        SNSUserInfo snsUserInfo = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // 通过网页授权获取用户信息
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                snsUserInfo = new SNSUserInfo();
                // 用户的标识
                snsUserInfo.setOpenId(jsonObject.getString("openid"));
                // 昵称
                snsUserInfo.setNickname(jsonObject.getString("nickname"));
                // 性别（1是男性，2是女性，0是未知）
                snsUserInfo.setSex(jsonObject.getInt("sex"));
                // 用户所在国家
                snsUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                snsUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                snsUserInfo.setCity(jsonObject.getString("city"));
                // 用户头像
                snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
                // 用户特权信息
                snsUserInfo.setPrivilegeList(JSONArray.toList(jsonObject.getJSONArray("privilege"), List.class));
            } catch (Exception e) {
                snsUserInfo = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取用户信息失败 errcode:"+errorCode+" errmsg:"+errorMsg);
            }
        }
        return snsUserInfo;
    }
    /*查询商户微信信息*/
    @RequestMapping("/wechat/queryMerWXMsg.do")
    public void queryMerWXMsg(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
//            List<Map<String, Object>> list =iWeChatService.queryGameType();
//            rt.addList_Map("gameType",list);
            rt.put("WXAppid","");
            rt.put("AppSecret","");
            rt.setSuccessMark();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonTools.outPutJson(rt.getData(), response);
    }


    /*获取游戏信息*/
    @RequestMapping("/wechat/getGameInfo.do")
    public void getGameInfo(HttpServletRequest request,HttpServletResponse response)throws Exception{
                Message rt = new Message();


                JsonTools.outPutJson(rt.getData(), response);


    }


    @RequestMapping("/wechat/getCountDownInfo.do")
    public void getCountDownInfo(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {

                JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*查看最新开奖号码--走势*/
    @RequestMapping("/wechat/getNewLotterys.do")
    public void getNewLotterys(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {


                JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/wechat/getAccountingRecord.do")
    public void getAccountingRecord(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            String startDate = request.getParameter("startTime");
            String endDate = request.getParameter("endTime");
            String player = request.getParameter("player");
            int startIndex = HttpUtils.getAsInt("$STARTINDEX");
            int endIndex = HttpUtils.getAsInt("$ENDINDEX");
            Object list  =iWeChatService.getAccountingRecord(startDate,endDate,player,startIndex,endIndex,false);
            rt.addPageDBGridData((List<Map>) list);
            Object size = iWeChatService.getAccountingRecord(startDate,endDate,player, - 1, -1, true);
            rt.addPageTotalNum(size);
            rt.setStatus(true);
            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/wechat/getOrderList.do")
    public void getOrderList(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            String startDate = request.getParameter("startTime");
            String endDate = request.getParameter("endTime");
            String player = request.getParameter("player");
            int startIndex = HttpUtils.getAsInt("$STARTINDEX");
            int endIndex = HttpUtils.getAsInt("$ENDINDEX");
            Object list  =iWeChatService.getOrderRecord(startDate, endDate, player, startIndex, endIndex, false);
            rt.addPageDBGridData((List<Map>) list);
            Object size = iWeChatService.getOrderRecord(startDate, endDate, player, -1, -1, true);
            rt.addPageTotalNum(size);
            rt.setStatus(true);
            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*查询在线人数*/
    @RequestMapping("/wechat/getOnlineNum.do")
    public void getOnlineNum(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            int onlineNum =iWeChatService.getOnlineNum();
            rt.put("onlineNum",onlineNum);
            rt.setStatus(true);
            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*登入*/
    @RequestMapping("/wechat/login.do")
    public void login(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            String account = request.getParameter("account");
            String pwd = request.getParameter("pwd");
            String ipAddr = HttpUtils.getRequestIp(request);
            rt =iWeChatService.accountLogin(account, pwd,ipAddr);
            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*游戏开关*/
    @RequestMapping("/wechat/queryGameParam.do")
    public void queryGameParam(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            String gameId = request.getParameter("GAME_ID");
            Map gameMp= new HashMap();
            gameMp=iWeChatService.queryGameInfo(gameId);
            rt.addMap("gameParam",gameMp);
            rt.setStatus(true);
            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/wechat/queryWechatInfo.do")
    public void queryWechatInfo(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {
            Map hfAccMap=iWeChatService.getHFaccMsg(); //获取平台账
            String PLAT_NAME=iWeChatService.getPlatName(); //获取平台名称
            String  APP_ID= hfAccMap.get("APP_ID").toString() ; //微信APPID
            String  PWD = hfAccMap.get("PWD").toString();  //微信密钥
            String  ADDR=hfAccMap.get("ADDR").toString(); //华菲微信前台地址
            rt.put("appid",APP_ID);
            rt.put("pwd", PWD);
            rt.put("addr",ADDR);
            rt.put("plat_name",PLAT_NAME);
            rt.setStatus(true);
            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/wechat/getPlatParam.do")
    public void getPlatParam(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {

            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @RequestMapping("/wechat/getOrderListTest.do")
    public void getOrderListTest(HttpServletRequest request,HttpServletResponse response)throws Exception{
        Message rt = new Message();
        try {


            JsonTools.outPutJson(rt.getData(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
