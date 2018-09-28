package com.dc.f01.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dc.f01.utils.HttpUtils;
import com.dc.f01.utils.MemcachedUtil;
import com.dc.f01.utils.PropertiesUtils;
import com.dc.f01.utils.StringTools;

public class WechatServiceImpl extends QuartzJobBean {

	private static transient Log log = LogFactory.getLog(WechatServiceImpl.class);
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		log.info("#### wechat task start!");
//		String accessToken = getAccessToken();// 获取token
//		if(accessToken!=null){
//			MemcachedUtil.put("access_token",accessToken);
//			String ticket= getTicket(accessToken);
//			if(ticket != null){
//				MemcachedUtil.put("jsapi_ticket",ticket);
//			}
//		}
		String accessToken = initWechatToken();
		for(int i=0;i<2;i++){
			if(accessToken == null){
				try{
					Thread.sleep(3000);
					accessToken = initWechatToken();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		if(accessToken == null){
			throw new JobExecutionException("accessToken获取失败");
		}
		String ticket = initWechatTicket(accessToken);
		for(int i=0;i<2;i++){
			if(ticket == null){
				try{
					Thread.sleep(3000);
					ticket = initWechatTicket(accessToken);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		log.info("#### wechat task end!");
	}
	
	private String initWechatToken(){
		String accessToken = getAccessToken();// 获取token
		if(accessToken != null){
			MemcachedUtil.put("access_token",accessToken);
		}
		return accessToken;
	}
	private String initWechatTicket(String accessToken){
		String ticket= getTicket(accessToken);
		if(ticket != null){
			MemcachedUtil.put("jsapi_ticket",ticket);
		}
		return ticket;
	}
	
	private String getAccessToken(){
		String appid= PropertiesUtils.findPropertiesKey("wxAppid");
		String appsecrt=PropertiesUtils.findPropertiesKey("wxAppsecret");
		String appidUrl="https://api.weixin.qq.com/cgi-bin/token";
		String accessToken = HttpUtils.getToken(appidUrl, appid, appsecrt);// 获取token
		if(StringTools.isBlankNull(accessToken)){
			log.info("微信初始化access_token失败");
		}
		return accessToken;
	}
	
	private String getTicket(String accessToken){
		if(StringTools.isBlankNull(accessToken)){
			return null;
		}
		String ticket= HttpUtils.getTicket("https://api.weixin.qq.com/cgi-bin/ticket/getticket", accessToken);
		if(StringTools.isBlankNull(ticket)){
			log.info("微信初始化jsapi_ticket失败");
		}
		return ticket;
	}

}
