package com.dc.f01.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringUtil {

	 private static Log logger = LogFactory.getLog(SpringUtil.class);  
	    /** Spring框架应用上下文对象 */  
	    private static ApplicationContext factory = getApplicationContext();  
	      
	    static{  
	        getApplicationContext();  
	    }  
	      
	    public static void setFactoryBean(ApplicationContext factory){  
	        SpringUtil.factory = factory;  
	    }  
	    /** 
	     * 获得Spring框架应用上下文对象  
	     * @return ApplicationContext 
	     */  
	    public static ApplicationContext getApplicationContext()  
	    {  
	        //判断如果 ApplicationContext 的对象 ＝＝ NULL  
	        if ( factory == null )  
	        {  
	            if(logger.isDebugEnabled()) logger.debug("===================================Init Spring's ApplicationContext=========================================");  
	            try  
	            {  
	            	//String path = PropertiesUtils.getProperty("path");
	                //factory = new FileSystemXmlApplicationContext(new String[]{path + "/applicationContext.xml" });  
	            	factory = new FileSystemXmlApplicationContext(new String[]{"applicationContext.xml" });  
	            }  
	            catch ( Exception e1 )  
	            {  
	                if(logger.isDebugEnabled()) logger.debug("err = " + e1.getMessage());  
	                e1.printStackTrace();  
	            }  
	        }  
	        //返回ApplicationContext  
	        return factory;  
	    }  
	    
	    
}
