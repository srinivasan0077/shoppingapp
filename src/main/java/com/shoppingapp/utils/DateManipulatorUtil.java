package com.shoppingapp.utils;

import java.util.Calendar;
import java.util.Date;

public class DateManipulatorUtil {

	private Calendar cal;
	
	public DateManipulatorUtil() {
		cal=Calendar.getInstance();
	}
	
	
	
	public  Date addMinutes(Date date,int n) {
		cal.setTime(date);
		cal.add(Calendar.MINUTE, n);
		return cal.getTime();
	}
	
	public Date addSeconds(Date date,int n) {
		cal.setTime(date);
		cal.add(Calendar.SECOND, n);
		return cal.getTime();
	}

}
