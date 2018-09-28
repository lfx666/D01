package com.dc.f01.interceptors;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.dc.f01.common.CodeConstant;
import com.dc.f01.utils.NumbericTools;
import com.dc.f01.utils.StringTools;



public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	private static transient Log log = LogFactory.getLog(HandshakeInterceptor.class);
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// 解决The extension [x-webkit-deflate-frame] is not supported问题
		if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
			request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
		}
		if (request instanceof ServletServerHttpRequest) {
	            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
	            HttpSession session = servletRequest.getServletRequest().getSession(false);
	            if (session != null) {
	                //使用userName区分WebSocketHandler，以便定向发送消息
	                Long userId = NumbericTools.obj2Long(session.getAttribute(CodeConstant.USER_ID));
	                String device = StringTools.obj2String(session.getAttribute("DEVICE"));
	                if (userId==null) {
	                	userId = 999999l;
	                    //TODO 请记住！！！登录功能完成后把下面的注释打开！！！！
	                    //当session获取不到用户时 强行把websocket断掉
	                    //return false;
	                }
	                if(device == null){
	                	device = "Web";
	                }
	                attributes.put(CodeConstant.SOCKET_USER,userId);
	                attributes.put("DEVICE", device);
	            }
	     }
		log.debug("Before Handshake");
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {
		log.debug("After Handshake");
		super.afterHandshake(request, response, wsHandler, ex);
	}
}
