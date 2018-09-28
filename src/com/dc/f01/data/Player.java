package com.dc.f01.data;

import java.util.Date;

public class Player {

	private String uid;
	
	private String name;
	
	private String status;

	private Date boundTime;
	
	private Date unboundTime;
	
	private String entryType;

	private String mobile;

	private Long userId;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getBoundTime() {
		return boundTime;
	}

	public void setBoundTime(Date boundTime) {
		this.boundTime = boundTime;
	}

	public Date getUnboundTime() {
		return unboundTime;
	}

	public void setUnboundTime(Date unboundTime) {
		this.unboundTime = unboundTime;
	}

	public String getEntryType() {
		return entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
}
