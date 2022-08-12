package com.hide_and_fps.project.util;

@SuppressWarnings("serial")
public class WebClientNeedRetryException extends RuntimeException{
	private final int statusCode;
	
	private boolean isNeedRetry;
	
	public WebClientNeedRetryException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
		this.isNeedRetry = true;
	}
	public WebClientNeedRetryException(String message, int statusCode, Boolean isNeedRetry) {
		super(message);
		this.statusCode = statusCode;
		this.isNeedRetry = isNeedRetry == null ? true : isNeedRetry.booleanValue();
	}
	
	public int getStatusCode() {
		return this.statusCode;
	}
	
	public void toggleIsNeedRetry() {
		this.isNeedRetry = !this.isNeedRetry;
	}
	
	public void setIsNeedRetryEnd() {
		this.isNeedRetry = false;
	}
	
	public void setIsNeedRetryStart() {
		this.isNeedRetry = true;
	}	
}
