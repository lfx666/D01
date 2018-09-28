package com.dc.f01.interceptors;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dc.f01.common.CodeConstant;
import com.dc.f01.common.CommonHelper;
import com.dc.f01.data.Player;
import com.dc.f01.service.ILoginService;
import com.dc.f01.utils.DateUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * add user info to application layer, when user success logon and logout
 * trigger listener, if user close browser not logout , when session timeout
 * auto logout
 * 
 * @author GARY
 * 
 */
public final class UserSessionBinding implements javax.servlet.http.HttpSessionBindingListener {
	private Log log = LogFactory.getLog(UserSessionBinding.class);
   
	private Player player;
	
	public UserSessionBinding(Player user) {
		this.player = user;
	}

	public synchronized void valueBound(HttpSessionBindingEvent event) {
		HttpSession session = event.getSession();
		ServletContext application = session.getServletContext();
		// 把用户名放入在线列表
		Map<String,Player> onlineUserList = (Map<String,Player>) application.getAttribute(CodeConstant.ONLINE_USER_LIST);
		// 第一次使用前，需要初始化
		if (onlineUserList == null) {
			onlineUserList = new ConcurrentHashMap();
			application.setAttribute(CodeConstant.ONLINE_USER_LIST, onlineUserList);
		}
        this.player.setBoundTime(DateUtil.curSysDate());
		onlineUserList.put(this.player.getUid(),this.player);
        if(log.isInfoEnabled()){
            log.info("concurrent User [" + onlineUserList.size() + "] login user[" + this.player.getUid() + "] entry[" + this.player.getEntryType() + "]");
        }
	}

	/**
	 * 1.session.invalidate() 2.session timeout 3.session.setAttribute
	 * 4.session.removeAttribute
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
        //添加时防止被触发
        if(event.getValue() == null){
            return;
        }
		HttpSession session = event.getSession();
		ServletContext application = session.getServletContext();
        Map<String,Player> onlineUserList = (Map<String,Player>) application.getAttribute(CodeConstant.ONLINE_USER_LIST);
        this.player.setUnboundTime(DateUtil.curSysDate());
		onlineUserList.remove(this.player.getUid());
		CommonHelper.removeSession(this.player.getUid());
		logout(session,onlineUserList.size());
	}

	/**
	 * add new comer user into listener
	 * @param request
	 */
	public static void addUserBinding(HttpServletRequest request,Player user) {
		request.getSession(false).setAttribute(CodeConstant.ONLINE_USER_BINDING_LISTENER, new UserSessionBinding(user));
	}

	/**
	 * update DB user status and remove attribute from session and coherence
	 * @param session
	 */
	private void logout(HttpSession session,int onlineSize) {
		try {
            //清除延迟推送失败信息的缓存
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
            ILoginService loginService = (ILoginService) wac.getBean("loginService");
			loginService.updateLoginStatus(this.player.getUid(),"0");
		long interval = (this.player.getUnboundTime().getTime() - this.player.getBoundTime().getTime()) / 1000;
        if(log.isInfoEnabled()) {
            log.info("concurrent User [" + onlineSize + "] logout user[" + this.player.getUid() + "] entry[" + this.player.getEntryType() + "] from[" +DateUtil.formatDate(this.player.getBoundTime())
                    + "] to[" + DateUtil.formatDate(this.player.getUnboundTime()) + "] interval[" + interval + "]");
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
