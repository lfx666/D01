package com.dc.f01.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dc.f01.common.SpringContextHolder;

public class TaskServiceImpl extends QuartzJobBean implements ITaskService{

	private IGameService gameService;
	
	private static transient Log log = LogFactory.getLog(TaskServiceImpl.class);
	
	

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
			log.info("####task start!");
			gameService = (IGameService) SpringContextHolder.getBean("gameService");
			
	        try {
	        	
			} catch (Exception e) {
				e.printStackTrace();
			}
	        log.info("####task run already!");
	}

	
}
