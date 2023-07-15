package com.shoppingapp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;

public class ExceptionCause extends Exception {


	private static final long serialVersionUID = 1L;
	
	private HttpStatus errorCode;
	private int status;

	public ExceptionCause(String message,HttpStatus errorCode) {
		super(message);
		this.errorCode=errorCode;
	}
	
	public ExceptionCause(String message,int status,HttpStatus errorCode) {
		super(message);
		this.errorCode=errorCode;
		this.setStatus(status);
	}
	
	public HttpStatus getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(HttpStatus errorCode) {
		this.errorCode = errorCode;
	}
	
	public static String getStackTrace(Exception e)
	{
	    StringWriter sWriter = new StringWriter();
	    PrintWriter pWriter = new PrintWriter(sWriter);
	    e.printStackTrace(pWriter);
	    return sWriter.toString();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
