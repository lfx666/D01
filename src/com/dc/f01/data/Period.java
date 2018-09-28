package com.dc.f01.data;

import java.io.Serializable;
import java.util.Date;

public class Period implements Serializable,Comparable<Period>{
	private static final long serialVersionUID = 1L;
	private String gameCode;
	
	private String periodNo;
	
	private Date startDate;
	
	private Date endDate;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPeriodNo() {
		return periodNo;
	}



	public void setPeriodNo(String periodNo) {
		this.periodNo = periodNo;
	}






	public String getGameCode() {
		return gameCode;
	}



	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}




	@Override
	public int compareTo(Period o) {
		if(this.periodNo.compareToIgnoreCase(o.getPeriodNo())> 0){
			return 1;
		}else if(this.periodNo.compareToIgnoreCase(o.getPeriodNo())< 0){
			return -1;
		}
		return 0;
	}

	
}
