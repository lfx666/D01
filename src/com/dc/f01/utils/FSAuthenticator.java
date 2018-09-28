package com.dc.f01.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class FSAuthenticator extends Authenticator{

	String userName=null;  
    String password=null;  
    
    public FSAuthenticator(String username, String password) {   
        this.userName = username;   
        this.password = password;   
    }   
    
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
	  return new PasswordAuthentication(userName, password);
	}
	
}
