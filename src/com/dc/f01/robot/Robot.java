package com.dc.f01.robot;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dc.f01.common.SpringContextHolder;
import com.dc.f01.data.*;
import com.dc.f01.service.IWeChatService;
import com.dc.f01.utils.DataTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;

import com.ddm.RNGUtils;
import com.dc.f01.common.CommonHelper;
import com.dc.f01.interceptors.NoticeMessageHandler;
import com.dc.f01.utils.DateUtil;

import net.sf.json.JSONObject;

public class Robot {

	private static Logger logger = LoggerFactory.getLogger(Robot.class);
	private static ScheduledExecutorService THREAD_POOL = Executors.newScheduledThreadPool(2);
	/**
	 * 游戏间隔
	 */
	private static Map<String,Object> gameIntervalMap = new HashMap<String,Object>();

	/**
	 * 对应游戏每秒跳
	 */
	private static Map<String,Object> timeMoveMap = new HashMap<String,Object>();

	private static List<VitrualSet> VITRUAL_SET_LIST;
	
	private static List<AiRobot> AI_LIST;

	private static List<BigAccount> BIG_ACC;
	
	private static List<Game> GAME_LIST;
	@Autowired
	private IWeChatService iWeChatService;
	public static void init(List<VitrualSet> vitrualSetList,List<AiRobot> aiList,List<Game> gameList){
		VITRUAL_SET_LIST = vitrualSetList;
		AI_LIST = aiList;
		GAME_LIST = gameList;
	}
	/**
	 * 机器人工作
	 */
	public static void run(){
		try{
			//倒计时机器人
			final Runnable task = new Runnable()
	        {  
	            @Override  
	            public void run()  
	            {
//					logger.debug("task run");
	            	Period period = null;
//					System.out.println((period.getEndDate().getTime() - DateUtil.getDefaultSysDate().getTime())/1000 - 30 );
	            	//倒计时结束
	            	for(int i = 0; i < GAME_LIST.size(); i++){



	            	}
	            	
	            }  
	        };
	        //AI投注
	        final Runnable aiBettingTask = new Runnable(){
				@Override
				public void run() {
				for(int l=0;l<GAME_LIST.size();l++) {

					}
				}
	        };
	        THREAD_POOL.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);//准时执行
	        THREAD_POOL.scheduleWithFixedDelay(aiBettingTask, 0, 1, TimeUnit.SECONDS);
		} finally {
			logger.info("Robot schedule start");
		}
	}

}
