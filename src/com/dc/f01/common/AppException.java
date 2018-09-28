package com.dc.f01.common;

public class AppException extends Exception{

	private static final long serialVersionUID = -2395784715034833410L;

	private String errorCode;
	
	public AppException() {
		super();
	}
	
	public AppException(String errorCode) {
		super(errorCode);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
	
	
}
