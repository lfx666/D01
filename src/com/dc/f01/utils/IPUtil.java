package com.dc.f01.utils;
import javax.servlet.http.HttpServletRequest;

public class IPUtil {
	//HTTP_X_FORWARDED_FOR HTTP_X_REAL_IP X-Real-IP
	    public static String getRemortIP(HttpServletRequest request) {
	        if (request.getHeader("X-Forwarded-For") == null) { return request.getRemoteAddr(); }
	        return request.getHeader("X-Forwarded-For");
	    }
	 
	    // 下面这个方法所说能取得浏览器端真正的IP地址。
	    public static String getIpAddr(HttpServletRequest request) {
	        String ip = request.getHeader("X-Forwarded-For");
	    	
	    	if(StringTools.isNotBlankNull(ip) && !"unKnown".equalsIgnoreCase(ip)){
	    		String[] ipArr = ip.split(",");
	    		if(ipArr != null && ipArr.length > 0){
	    			return ipArr[0];
	    		}
	    	}
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("Proxy-Client-IP");
	        }
	         
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("WL-Proxy-Client-IP");
	        }
	         
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getRemoteAddr();
	        }
	        return ip;
	    }
}
