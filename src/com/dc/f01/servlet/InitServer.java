package com.dc.f01.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.dc.f01.data.*;
import com.dc.f01.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.f01.common.CommonHelper;
import com.dc.f01.common.SpringContextHolder;
import com.dc.f01.robot.Robot;
import com.dc.f01.service.IGameService;


/**
* Copyright (C), 2016, Fixed-Star
* FileName: InitServer.java
* 系统初始化
* @author Gary
* @Date    2016/05/29
* @version 1.00
*/
@WebServlet(urlPatterns={"/InitServer"},loadOnStartup = 3)
public class InitServer extends HttpServlet {
	protected static transient Log log = LogFactory.getLog(InitServer.class); 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IGameService gameService;
	/**
	 * Constructor of the object.
	 */
	public InitServer() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		log.info("#################server started");
	        // 调用sysParameterService取出所有的系统参数
	        gameService = (IGameService) SpringContextHolder.getBean("gameService");
	        try {
				List listp=gameService.queryAIList();

			} catch (Exception e) {
				e.printStackTrace();
			}
	    //参数初始化
	    //TODO 游戏参数 调用webservice
		//获取游戏期号列表
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

		//启动机器人
//		Robot.run();
	}

}
