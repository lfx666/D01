package com.dc.f01.dao;

import com.dc.f01.common.StoredProcedureTemplate;
import com.dc.f01.data.request.PlayReq;
import com.dc.f01.data.request.ScoreReq;
import com.dc.f01.interceptors.NoticeMessageHandler;
import com.dc.f01.utils.DataTools;
import com.dc.f01.utils.MD5Util;
import com.dc.f01.utils.StringTools;
import com.dc.f01.data.SNSUserInfo;
import net.sf.json.JSONObject;
import org.apache.axis.utils.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.TextMessage;

import java.util.*;

@Repository("iWeChatDao")
public class WeChatDaoImpl extends BaseDaoImpl implements IWeChatDao {

    @Override
    public String registerAcc(SNSUserInfo snsUserInfo)throws Exception {
        StoredProcedureTemplate temp = new StoredProcedureTemplate();
        temp.setFunction(false);
        temp.setJdbcTemplate(getJdbcTemplate());
        temp.setSql("PCK_PLAYER_CENTER.proc_player_create");
        temp.setVarcharParam("i_usertag", snsUserInfo.getOpenId());
        temp.setVarcharParam("i_nickname", snsUserInfo.getNickname());
        temp.setVarcharParam("i_pwd", "");
        temp.setVarcharParam("i_head", snsUserInfo.getHeadImgUrl());
        temp.setVarcharParam("i_sex", DataTools.getAsString(snsUserInfo.getSex()));
        temp.setVarcharParam("i_country",snsUserInfo.getCountry());
        temp.setVarcharParam("i_province",snsUserInfo.getProvince());
        temp.setVarcharParam("i_city", snsUserInfo.getCity());
        temp.setVarcharOutParam("o_result");
        Map map = temp.execute();
        return map.get("o_result").toString();
    }

    @Override
    public Map queryMemberInfo(String userTag) throws Exception {
        String sql="select m.user_tag, m.user_id,m.nick_name,m.head_img,to_char(trunc（m.balance,2）,'fm999,999,990.00')as balance,m.gender   from fs_s20_member_info m where m.user_tag=?";
        return this.getJdbcTemplate().queryForMap(sql,userTag);
    }

    @Override
    public int getOnlineNum() throws Exception {
        //在线人数 = 游戏基数+随机数（1~10）+ 当前在线人数*随机数（1~3）
        int onlineNum=0;
        String sql="select nvl(t.base_num,1) from fs_s20_platform t";
        int baseNum= this.getJdbcTemplate().queryForObject(sql, new Object[]{}, Integer.class) ;
        //当前在线人数：
        String sql_="select count(*) from fs_s20_member_info t where t.login_st='1' ";
        int curOnline= this.getJdbcTemplate().queryForObject(sql_, new Object[]{},Integer.class) ;
        int max_a=10; int min_a=1;
        int max_b=3; int min_b=1;
        Random random = new Random();
        int randomNumOne = random.nextInt(max_a)%(max_a-min_a+1) + min_a;
        int randomNumTwo = random.nextInt(max_b)%(max_b-min_b+1) + min_b;
        onlineNum= baseNum+randomNumOne+curOnline*randomNumTwo;
        return onlineNum;
    }

    @Override
    public Object getAccountingRecord(String startDate, String endDate, String player, int startIndex, int endIndex, boolean b) throws Exception {
        List<Object> paramList = new ArrayList<Object>();
        String sql = " select t.trans_id , tt.trans_name,to_char(trunc(abs(decode(sign(t.amount),1,t.amount,0)),2),'fm999,999,990.00') in_amount," +
                " to_char(trunc(abs(decode(sign(t.amount),-1,t.amount,0)),2),'fm999,999,990.00') out_amount,to_char(t.trans_date,'hh24:mi:ss') as trans_date," +
                " t.cur_balance from fs_s20_transaction t,Fs_S20_Trans_Type tt where t.trans_type=tt.trans_code and  t.user_id=?" +
                " and t.trans_date>=to_date(?, 'YYYY/MM/DD HH24:MI:SS')" +
                " and t.trans_date<=to_date(?, 'YYYY/MM/DD HH24:MI:SS') order by t.trans_date desc,t.trans_id DESC ";
        paramList.add(player);
        paramList.add(startDate);
        paramList.add(endDate);
        Object obj[] = paramList.toArray();
        return this.queryDataByArray(sql.toString(), obj, startIndex, endIndex, b);
    }

    @Override
    public Object getOrderRecord(String startDate, String endDate, String player, int startIndex, int endIndex, boolean b) throws Exception {
        List<Object> paramList = new ArrayList<Object>();
        String sql = "select t.order_no," +
                "       decode(t.status, '002', '未开奖', '004', '已中奖', '005', '未中奖', '012', '空开撤单', '010', '系统撤单') as status," +
                "        substr(t.period, instr(t.period,'-',1,1)+1, length(t.period) ) as period ," +
                "       t.play," +
                "       t.content," +
                "       to_char(nvl(t.bet_amount,0), 'fm999,999,990.00') as bet_amount," +
                "       to_char(nvl(t.win_amount,0), 'fm999,999,990.00') as win_amount" +
                "  from fs_s20_wx_order t" +
                " where t.create_date >= to_date(?, 'YYYY/MM/DD HH24:MI:SS')" +
                "   and t.create_date <= to_date(?, 'YYYY/MM/DD HH24:MI:SS')" +
                "   and t.user_id = ?" +
                "    order by t.create_date desc";
        paramList.add(startDate);
        paramList.add(endDate);
        paramList.add(player);
        Object obj[] = paramList.toArray();
        return this.queryDataByArray(sql.toString(),obj, startIndex, endIndex, b);
    }

    @Override
    public Map getPlayerInfo(String userid) throws Exception {
        String sql="select m.user_tag, m.user_id,m.nick_name,m.head_img,to_char(trunc（m.balance,2）,'fm999,999,990.00')as balance,m.gender   from fs_s20_member_info m where m.user_id=?";
        return this.getJdbcTemplate().queryForMap(sql,userid);
    }

    @Override
    public String insertOrderRecord(String orderNo, PlayReq playReq,String  userId) throws Exception {

        String totalBetMoney = "";
        String[] orderNoArr =orderNo.split(",");
        String[] FrontBetArr=new String[2];
        String[] noteNumARR=new String[2];//注数
        String oneToFiveStr="";
        String sixToZero="";
        if(orderNoArr.length==2){
            String posStr=playReq.getPos();
            String[] posArr = new String[posStr.length()];
            for(int i = 0; i < posStr.length(); i++)
            {
                //先由字符串转换成char,再转换成String,然后Integer
                posArr[i] = DataTools.getAsString(String.valueOf(posStr.charAt(i)));
            }
            for( int j=0; j<posArr.length;j++){
                if(DataTools.getAsInt(posArr[j])>=1&&DataTools.getAsInt(posArr[j])<=5){
                    oneToFiveStr+= posArr[j];
                }else{
                    sixToZero+=posArr[j];
                }
            }
            FrontBetArr[0]=sixToZero+"/"+playReq.getBuyNum();
            FrontBetArr[1]=oneToFiveStr+"/"+playReq.getBuyNum();
            if("DXDS".equals(playReq.getPlay())){
                noteNumARR[0]=DataTools.getAsString(sixToZero.length() * 5);
                noteNumARR[1]=DataTools.getAsString(oneToFiveStr.length() * 5);
            }else if ("DWD".equals(playReq.getPlay())){
                noteNumARR[0]=DataTools.getAsString(sixToZero.length() * playReq.getBuyNum().length());
                noteNumARR[1]=DataTools.getAsString(oneToFiveStr.length() * playReq.getBuyNum().length());
            }
        }
        for (int k=0; k< orderNoArr.length;k++) {
            //查找交易表和订单表关联号码
            String refSql = " select SEQ_TRANSACTION.Nextval from dual ";
            String refId = this.getJdbcTemplate().queryForObject(refSql, new Object[]{}, String.class);
            //投注的总额 定位胆和大小单双计算方式分开算
            if(playReq.getGmId().equals("BJPK10")&&orderNoArr.length==2){
                if ("DXDS".equals(playReq.getPlay())) {
                    totalBetMoney = DataTools.getAsString(DataTools.getAsDouble(playReq.getByt1TTMoney())*DataTools.getAsInt(noteNumARR[k])/DataTools.getAsInt(playReq.getByt1TTNum()));
                    playReq.setFrontBetContent(FrontBetArr[k]);
                } else if ("DWD".equals(playReq.getPlay())) {
                    playReq.setByt1TTNum(noteNumARR[k]);
                    totalBetMoney = DataTools.getAsString(DataTools.getAsInt(playReq.getByt1TTMoney()) * DataTools.getAsInt(playReq.getByt1TTNum()));
                    playReq.setFrontBetContent(FrontBetArr[k]);
                }
            }else {
                if ("DXDS".equals(playReq.getPlay())) {
                    totalBetMoney = playReq.getByt1TTMoney();
                } else if ("DWD".equals(playReq.getPlay())) {
                    totalBetMoney = DataTools.getAsString(DataTools.getAsInt(playReq.getByt1TTMoney()) * DataTools.getAsInt(playReq.getByt1TTNum()));
                }
            }
            //插入订单表
            String sql = " insert into fs_s20_wx_order (ORDER_NO, USER_ID, CREATE_DATE, END_DATE, PLAY, CONTENT, GAME_CODE, PERIOD, BET_AMOUNT, STATUS, REF_ID, WIN_NO) " +
                    " values (?, ?,sysdate, '', ?, ?, ?, ?, ?, '002', ?, '')";
            this.executeSQLUpdate(sql, new Object[]{orderNoArr[k], userId, playReq.getPlay(), playReq.getFrontBetContent(),
                    playReq.getGmId(), playReq.getByt1CurPno(), totalBetMoney, refId});
            //扣减金额
            String sql_1 = " update fs_s20_member_info m set m.balance=m.balance-? where m.user_id=?";
            this.executeSQLUpdate(sql_1, new Object[]{totalBetMoney, userId});

            //查找用户金额
            String sql_2 = " select t.balance from fs_s20_member_info t where t.user_id=? ";
            String curCoins = this.getJdbcTemplate().queryForObject(sql_2, new Object[]{userId}, String.class);

            //插入交易表
            String sql_3 = " insert into fs_s20_transaction (TRANS_ID, USER_ID, TRANS_TYPE, TRANS_DATE, PLAN_NO, AMOUNT, CUR_BALANCE, GAME_ID, PERIOD_NO, REASON, REMARK, BIZ_DATE, ACC_TYPE, BILL_IP, SERIES_VALUE, BETTING_ACCOUNTS, BETTING_AMOUNT)" +
                    " values (?, ?, '06', sysdate, ?, ?, ?, '', '', '', '投注', trunc(sysdate), '01', '', null, '', null) ";
            this.executeSQLUpdate(sql_3, new Object[]{refId, userId, orderNoArr[k], "-" + totalBetMoney, curCoins});
        }
        //重新设置为原来的样子
        if(playReq.getGmId().equals("BJPK10")&&orderNoArr.length==2){
            playReq.setFrontBetContent(playReq.getPos()+"/"+playReq.getBuyNum());
        }
        //查找用户金额(千分符)
        String sql_4=" select to_char(t.balance,'fm999,999,999,990.00') from fs_s20_member_info t where t.user_id=? ";
        return this.getJdbcTemplate().queryForObject(sql_4, new Object[]{userId},String.class);
    }

    @Override
    public Map getGameParam(String gmID,String userId) throws Exception {
        String sql="select nvl(t.min_bet,0) as min_bet,nvl(t.max_bet,0) as max_bet,t.is_open  from fs_s20_vgame_param t where t.game_code=? ";
        String sql_1="select t.balance from fs_s20_member_info t where t.user_id=?";
        Map map=new HashMap();
        map.put("BET_LIMIT",this.getJdbcTemplate().queryForMap(sql,gmID));
        map.put("USER_COINS",this.getJdbcTemplate().queryForObject(sql_1, new Object[]{userId},String.class));
        return map;
    }

    @Override
    public String upDownScore(String userId, ScoreReq req) throws Exception {
        StoredProcedureTemplate temp = new StoredProcedureTemplate();
        temp.setFunction(false);
        temp.setJdbcTemplate(getJdbcTemplate());
        temp.setSql("PCK_PLAYER_CENTER.proc_player_apply_score");
        temp.setVarcharParam("i_userid", userId);
        temp.setNumberParam("i_amount", DataTools.getAsDouble(req.getScore()));
        temp.setVarcharParam("i_type", req.getScoreType());
        temp.setVarcharOutParam("o_result");
        temp.setVarcharOutParam("o_order");
        temp.setVarcharOutParam("o_coins");
        Map map = temp.execute();
        return map.get("o_result").toString()+"`"+ map.get("o_order").toString()+"`"+map.get("o_coins").toString();
    }

    @Override
    public Map getHFaccMsg() throws Exception {
        String sql="select t.hf_accounts,t.hf_pwd,t.hf_addr,t.app_id,t.pwd,t.addr from fs_s20_official t";
        return this.getJdbcTemplate().queryForMap(sql);
    }

    @Override
    public String login(SNSUserInfo snsUserInfo,String loginType,String ipStr,String userId) throws Exception {
        StoredProcedureTemplate temp = new StoredProcedureTemplate();
        temp.setFunction(false);
        temp.setJdbcTemplate(getJdbcTemplate());
        temp.setSql("PCK_PLAYER_CENTER.proc_player_login");
        temp.setVarcharParam("i_userid", userId);
        temp.setVarcharParam("i_password", "");
        temp.setVarcharParam("i_loginIp", ipStr);
        temp.setVarcharParam("i_openid", snsUserInfo.getOpenId());
        temp.setVarcharParam("i_loginType",loginType);
        temp.setVarcharOutParam("o_result");
        Map map = temp.execute();
        return DataTools.getAsString(map.get("o_result"));
    }

    @Override
    public int updateOrderRecord(List orderList,String gameCode,String period,String winNo,String openType) throws Exception {

        //更新开始奖号录入的时间
        if("x,x,x,x,x".equals(winNo)){ //空开撤单
            String updateAwardSQL="UPDATE FS_S20_AWARD_NUMBER T SET T.WIN_NO=?,  T.OPEN_TYPE=? ,T.INPUT_DATE=SYSDATE,T.VERUFY_DATE=SYSDATE,T.INTERVAL_DATE=to_char(trunc(sysdate)+(sysdate -t.DRAW_DATE),'mi:ss')   WHERE T.PERIOD=? AND T.GAME_NAME=?";
            this.executeSQLUpdate(updateAwardSQL,new Object[]{winNo,"VOID",period,gameCode});
        }else{
            String updateAwardSQL="UPDATE FS_S20_AWARD_NUMBER T SET T.WIN_NO=?,  T.OPEN_TYPE=? ,T.INPUT_DATE=SYSDATE,T.VERUFY_DATE=SYSDATE,T.INTERVAL_DATE=to_char(trunc(sysdate)+(sysdate -t.DRAW_DATE),'mi:ss')   WHERE T.PERIOD=? AND T.GAME_NAME=?";
            this.executeSQLUpdate(updateAwardSQL,new Object[]{winNo,openType,period,gameCode});
        }
        List paramList = new ArrayList();
        String sql = " SELECT O.ORDER_NO,O.USER_ID,O.PERIOD  FROM FS_S20_WX_ORDER O WHERE O.GAME_CODE=? AND O.PERIOD=? ";
        paramList.add(gameCode);
        paramList.add(period);
        List WXorder= this.queryDataByLt(sql.toString(), paramList);
        //遍历更改订单中奖不中奖信息
        String orderStatus="";
        for(int i=0,len=WXorder.size();i<len;i++){
            Map WXorderMap =(Map)WXorder.get(i);
            for(int j=0,leng=orderList.size();j<leng;j++){
                JSONObject orderObj =(JSONObject)orderList.get(j);
                if( WXorderMap.get("ORDER_NO").equals(orderObj.get("ORDERNO"))){
                    if(orderObj.get("STATUS").equals("已中奖")){
                        //增加金额
                        String sql_1=" update fs_s20_member_info m set m.balance=m.balance+? where m.user_id=?" ;
                        this.executeSQLUpdate(sql_1,new Object[]{DataTools.getAsDouble(orderObj.get("TOTAL_WIN_AMOUNT")), WXorderMap.get("USER_ID")});
                        //查找用户金额
                        String sql_2=" select t.balance from fs_s20_member_info t where t.user_id=? ";
                        String  curCoins=this.getJdbcTemplate().queryForObject(sql_2, new Object[]{WXorderMap.get("USER_ID")},String.class);
                        //插入交易表
                        String sql_3=" insert into fs_s20_transaction (TRANS_ID, USER_ID, TRANS_TYPE, TRANS_DATE, PLAN_NO, AMOUNT, CUR_BALANCE, GAME_ID, PERIOD_NO, REASON, REMARK, BIZ_DATE, ACC_TYPE, BILL_IP, SERIES_VALUE, BETTING_ACCOUNTS, BETTING_AMOUNT)" +
                                " values (SEQ_TRANSACTION.Nextval, ?, '07', sysdate, ?, ?, ?, '', '', '', '中奖', trunc(sysdate), '01', '', null, '', null) ";
                        this.executeSQLUpdate(sql_3,new Object[]{WXorderMap.get("USER_ID"),WXorderMap.get("ORDER_NO"),orderObj.get("TOTAL_WIN_AMOUNT"),curCoins});
                        Map echoMsg= new HashMap();
                        String winMsg="恭喜，您的订单"+WXorderMap.get("ORDER_NO")+"在"+WXorderMap.get("PERIOD")+"期中奖，获得"+orderObj.get("TOTAL_WIN_AMOUNT")+"分。";
                        echoMsg.put("msg",winMsg);
                        echoMsg.put("msgType","all");
                        //推个人信息
                        NoticeMessageHandler.sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),DataTools.getAsLong(WXorderMap.get("USER_ID")));
                    }
                    else if(orderObj.get("STATUS").equals("未中奖")){
                    }
                    else if(orderObj.get("STATUS").equals("空开撤单")){
                        //增加金额
                        String sql_1=" update fs_s20_member_info m set m.balance=m.balance+? where m.user_id=?" ;
                        this.executeSQLUpdate(sql_1,new Object[]{DataTools.getAsDouble(orderObj.get("TOTAL_BETT_AMOUNT")), WXorderMap.get("USER_ID")});
                        //查找用户金额
                        String sql_2=" select t.balance from fs_s20_member_info t where t.user_id=? ";
                        String  curCoins=this.getJdbcTemplate().queryForObject(sql_2, new Object[]{WXorderMap.get("USER_ID")},String.class);
                        //插入交易表
                        String sql_3=" insert into fs_s20_transaction (TRANS_ID, USER_ID, TRANS_TYPE, TRANS_DATE, PLAN_NO, AMOUNT, CUR_BALANCE, GAME_ID, PERIOD_NO, REASON, REMARK, BIZ_DATE, ACC_TYPE, BILL_IP, SERIES_VALUE, BETTING_ACCOUNTS, BETTING_AMOUNT)" +
                                " values (SEQ_TRANSACTION.Nextval, ?, '12', sysdate, ?, ?, ?, '', '', '', '空开撤单', trunc(sysdate), '01', '', null, '', null) ";
                        this.executeSQLUpdate(sql_3,new Object[]{WXorderMap.get("USER_ID"),WXorderMap.get("ORDER_NO"),orderObj.get("TOTAL_BETT_AMOUNT"),curCoins});

                        Map echoMsg= new HashMap();
                        String orderBackMsg="您的订单"+WXorderMap.get("ORDER_NO")+"已被撤单，投注分已回退。";
                        echoMsg.put("msg",orderBackMsg);
                        echoMsg.put("msgType","all");
                        //推个人信息
                        NoticeMessageHandler.sendMessage(new TextMessage(JSONObject.fromObject(echoMsg).toString()),DataTools.getAsLong(WXorderMap.get("USER_ID")));

                    }else if(orderObj.get("STATUS").equals("系统撤单")){
                        //插入交易表
//                        String sql_3=" insert into fs_s20_transaction (TRANS_ID, USER_ID, TRANS_TYPE, TRANS_DATE, PLAN_NO, AMOUNT, CUR_BALANCE, GAME_ID, PERIOD_NO, REASON, REMARK, BIZ_DATE, ACC_TYPE, BILL_IP, SERIES_VALUE, BETTING_ACCOUNTS, BETTING_AMOUNT)" +
//                                " values (SEQ_TRANSACTION.Nextval, ?, '15', sysdate, ?, ?, ?, '', '', '', '退回奖金', trunc(sysdate), '01', '', null, '', null) ";
//                        this.executeSQLUpdate(sql_3,new Object[]{WXorderMap.get("USER_ID"),WXorderMap.get("ORDER_NO"),orderObj.get("TOTAL_WIN_AMOUNT"),curCoins});

                    }
                    String updateSQL="UPDATE FS_S20_WX_ORDER O SET O.WIN_NO=?, O.WIN_AMOUNT=? ,O.STATUS=? ,O.END_DATE=SYSDATE  WHERE O.ORDER_NO=?";
                    this.executeSQLUpdate(updateSQL,new Object[]{winNo,orderObj.get("TOTAL_WIN_AMOUNT"),orderObj.get("OI_STATUS"),orderObj.get("ORDERNO").toString()});

                }
            }

        }
        //更新派奖时间（奖号兑完）
        String updateSQL="UPDATE FS_S20_AWARD_NUMBER T SET T.CASE_PRIZE_STATUS=?, T.SEND_DATE=SYSDATE,T.SEND_INTERVAL_DATE=to_char(trunc(sysdate)+(sysdate -t.verufy_date),'mi:ss')  WHERE T.PERIOD=? AND T.GAME_NAME=?";
        this.executeSQLUpdate(updateSQL,new Object[]{"兑奖成功",period,gameCode});

        return  1;
    }

    @Override
    public int insertAwardPeriod(String game_id, String enter_period) throws Exception {
        String awareSql=" insert into fs_s20_award_number (PERIOD,  GAME_NAME, DRAW_DATE) " +
                "values (?, ?, sysdate) " ;
        return  this.executeSQLUpdate(awareSql,new Object[]{enter_period,game_id});
    }

    @Override
    public String accountLogin(String account, String pwd, String ipAddr, String mobielWeb) throws Exception {
        StoredProcedureTemplate temp = new StoredProcedureTemplate();
        temp.setFunction(false);
        temp.setJdbcTemplate(getJdbcTemplate());
        temp.setSql("PCK_PLAYER_CENTER.proc_player_account_login");
        temp.setVarcharParam("i_account", account);
        temp.setVarcharParam("i_password", MD5Util.Md5(pwd) );
        temp.setVarcharParam("i_loginIp", ipAddr);
        temp.setVarcharParam("i_loginType", mobielWeb);
        temp.setVarcharOutParam("o_result");
        Map map = temp.execute();
        return DataTools.getAsString(map.get("o_result"));
    }

    @Override
    public Map getPlayerInfoByAcc(String account) throws Exception {
        String sql="select m.user_id,m.nick_name,m.head_img,to_char(trunc（m.balance,2）,'fm999,999,990.00')as balance,m.gender,m.LOGIN_FAIL_NUM   from fs_s20_member_info m where m.accounts=?";
        return this.getJdbcTemplate().queryForMap(sql,account);
    }

    @Override
    public String bindAccount(SNSUserInfo snsUserInfo, String pwd, String account) throws Exception {
        String sql="";
        sql="select count(*) from fs_s20_member_info  t where t.accounts=? ";
        int tag= this.getJdbcTemplate().queryForObject(sql, new Object[]{account},Integer.class) ;
        if(tag>0){
            return "-1"; //账号已存在
        }
         sql="select t.accounts from fs_s20_member_info  t where t.user_tag=? ";
         String acc= this.getJdbcTemplate().queryForObject(sql, new Object[]{snsUserInfo.getOpenId()},String.class) ;
        if(StringTools.isNotBlankNull(acc)){
            return "0"; //微信号已绑定账号
        }
        //更新账号密码
        String updateSQL="UPDATE fs_s20_member_info T SET T.accounts=?,T.pwd=? WHERE T.user_tag=?";
        return this.executeSQLUpdate(updateSQL,new Object[]{account,pwd,snsUserInfo.getOpenId()}).toString();
    }

    @Override
    public Map queryGameInfo(String gameId) throws Exception {
        String sql="select  t.is_open from fs_s20_vgame_param t where t.game_code=?";
        return this.getJdbcTemplate().queryForMap(sql,gameId);
    }

    @Override
    public String queryMemberOpenIdByAcc(String account) throws Exception {
        String sql_2=" select t.user_tag from fs_s20_member_info t where t.accounts=? ";
        return this.getJdbcTemplate().queryForObject(sql_2, new Object[]{account},String.class);
    }

    @Override
    public String getPlatName() throws Exception {
        String sql=" select nvl(t.plat_name,'') from fs_s20_platform t ";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{},String.class);
    }

    @Override
    public Long getGameDelayTime(String gameCode) throws Exception {
        String sql=" select t.locking_time from fs_s20_game t where t.game_code=?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{gameCode},Long.class);
    }

}
