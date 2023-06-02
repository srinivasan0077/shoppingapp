package com.shoppingapp.entities;

import com.google.gson.Gson;

public class Response {

	private String message;
	private int status;
	private Object content;
	public static int SUCCESS=2000;
	public static int INTERNAL_ERROR=5000;
	public static int BAD_REQUEST=4000;
	public static int UNAUTHORIZED=4001;
	public static int LOGGED=3000;
	public static int EXPIRY=4002;
	public static int ACCEPTED=2001;
	
	public Response(String message, int status, Object content) {
		this.message = message;
		this.setStatus(status);
		this.content = content;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String toString() {
		Gson gson=new Gson();
		return gson.toJson(this);
	}
	
}
