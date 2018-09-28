package com.dc.f01.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.dc.f01.data.*;
import com.dc.f01.utils.DataUtil;
import com.dc.f01.utils.DateUtil;
import com.dc.f01.utils.StringTools;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommonHelper {
	private static Logger logger = LoggerFactory.getLogger(CommonHelper.class);
	/**
	 * 参数列表
	 */
	private static Map<String,Object> paramMap = new HashMap<String,Object>();

	/**
	 * session列表
	 */
	private static Map<String, HttpSession> sessionMaps = new HashMap<String, HttpSession>();



	public synchronized static void putSession(String uId, HttpSession session){
		sessionMaps.put(uId, session);
	}
	
	public synchronized static HttpSession getSession(String uId){
		return sessionMaps.get(uId);
	}
	
	public synchronized static void removeSession(String uId){
		 if(sessionMaps.containsKey(uId))
		 {
			 sessionMaps.remove(uId);
		 }
	}






	

	


	
}
