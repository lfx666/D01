package com.dc.f01.action;



import com.dc.f01.common.AppException;

import com.dc.f01.utils.HttpUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class BaseAction extends MultiActionController {

	protected static transient Log log = LogFactory.getLog(BaseAction.class); 

	 protected HttpServletRequest request;  
	    protected HttpServletResponse response;  
	    protected HttpSession session;
	      
	    @ModelAttribute
	    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){  
	        this.request = request;
	        this.response = response;
	        this.session = request.getSession();
	    }
	    
	    @InitBinder  
        public void initBinder(WebDataBinder binder) {  
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            dateFormat.setLenient(false);  
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));  
        }  
	    
	    @ExceptionHandler(AppException.class)
	    public String expHandle(HttpServletRequest request,HttpServletResponse response, AppException ex){
	    	HttpUtils.errorAlert(response, ex.getErrorCode());
			return null;
	    }
}
