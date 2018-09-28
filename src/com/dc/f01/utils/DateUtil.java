package com.dc.f01.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    
    private static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static final  SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

    public static  String formatDate(Date date)throws ParseException{
        return sdf.format(date);
    }
    
    public static Date parse(String strDate) throws ParseException{

        return sdf.parse(strDate);
    }
   
    public static  String curDateStr()throws ParseException{
    	Calendar cdate = Calendar.getInstance();  
    	cdate.setTime(new Date());
        return sdf.format(cdate.getTime());
    }

	public static String startDateStr() {
		Calendar cdate = Calendar.getInstance();  
    	cdate.setTime(new Date());
        return sd.format(cdate.getTime()) + " 06:00:00";
	}

	public static String endDateStr() {
		Calendar cdate = Calendar.getInstance();  
    	cdate.setTime(new Date());
    	cdate.add(Calendar.DATE, 1);
        return sd.format(cdate.getTime()) + " 06:00:00";
	}

	public static Date curSysDate() {
		Calendar cdate = Calendar.getInstance();  
    	cdate.setTime(new Date());
        return cdate.getTime();
	}
	public static Date getDefaultSysDate() {
		return new Date(getDefaultSysDateLongTime());
	}
	public static long getDefaultSysDateLongTime() {
		return getDefaultSysTimestamp().getTime();
	}
	public static Timestamp getDefaultSysTimestamp() {
		Timestamp rtn = null;

		try {
			rtn = new Timestamp(System.currentTimeMillis());
			return rtn;
		} catch (Exception var5) {
			rtn = new Timestamp(System.currentTimeMillis());
			return rtn;
		} finally {
			;
		}
	}

	/**
	 * 获取时间 小时:分;秒 HH:mm:ss
	 *
	 * @return
	 */
	public static String getTimeShort() {
		Date currentTime = new Date();
		String dateString = sdf.format(currentTime);
		return dateString;
	}
	/**
	 * 获取时间 年月日 HH:mm:ss
	 *
	 * @return
	 */
	public static String getYMDShort() {
		Date currentTime = new Date();
		String dateString = sd.format(currentTime);
		return dateString;
	}
	/**
	 * 获取一天
	 *
	 * @return
	 */
	public static String getNextDay() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, +1);
		date = calendar.getTime();
		String dateString = sd.format(date);
		return dateString;
	}

	public static void main(String args[]) {
		Long ll= DataTools.getAsLong("1512554806000");
		Date date2 = new Date();
		date2.setTime(ll);
		System.out.println(sdf.format(date2));
	}
}
