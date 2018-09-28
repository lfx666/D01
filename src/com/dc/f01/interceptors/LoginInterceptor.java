package com.dc.f01.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dc.f01.common.CodeConstant;
import com.dc.f01.common.CommonHelper;
import com.dc.f01.data.Player;




public class LoginInterceptor  implements HandlerInterceptor  {

	private Logger log = Logger.getLogger(LoginInterceptor.class);
	
	
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
		log.debug("##############  interceptor postHandle " );
		String path = request.getRequestURI();
		HttpSession session = request.getSession(false);

		if(path.indexOf("/hall/") != -1){
			return;
		}
		Object obj = request.getSession().getAttribute("uid");
		Player player = null;
		if(obj != null){
            player =  (Player) obj;
			if(CodeConstant.USER_STATUS_3.equals(player.getStatus())){
				//session.invalidate();
			}
		}
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		String path = request.getRequestURI();
		String needEntriptPwd =request.getParameter("needEntriptPwd");
		HttpSession session = request.getSession(false);
		Object obj = null;
		if(session != null){
			obj = session.getAttribute("uid");
		}
        if(null != obj ){
        	Player player = (Player) obj;
        	HttpSession s = CommonHelper.getSession(player.getName());
			if(s != null && !s.getId().equals(session.getId())){
				session.invalidate();
				response.setHeader("sessionstatus", "relogin");//在响应头设置session状态  
				return false;
			}
        }
        return true;
	}

	

}
