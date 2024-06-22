package com.shoppingapp.utils;

import java.util.Calendar;
import java.util.Date;

public class DateManipulatorUtil {

	private static Calendar cal;
	
	static {
		cal=Calendar.getInstance();
    }
	
	public static Date addMinutes(Date date,int n) {
		cal.setTime(date);
		cal.add(Calendar.MINUTE, n);
		return cal.getTime();
	}
	
	public static Date addSeconds(Date date,int n) {
		cal.setTime(date);
		cal.add(Calendar.SECOND, n);
		return cal.getTime();
	}
	
	public static Date addHours(Date date,int n) {
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, n);
		return cal.getTime();
	}
	
	public static Date addDays(Date date,int n) {
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, n);
		return cal.getTime();
	}

}
