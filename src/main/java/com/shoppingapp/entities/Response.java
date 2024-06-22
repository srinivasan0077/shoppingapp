package com.shoppingapp.entities;

import com.google.gson.Gson;

public class Response {

	private String message;
	private int status;
	private Object content;
	private int noofrows;
	public static int SUCCESS=2000;
	public static int INTERNAL_ERROR=5000;
	public static int BAD_REQUEST=4000;
	public static int UNAUTHORIZED=4001;
	public static int EXPIRY=4002;
	public static int ACCEPTED=2001;
	public static int NOT_FOUND=4004;
	
	public Response(String message, int status, Object content) {
		this.message = message;
		this.setStatus(status);
		this.content = content;
	}
	
	public Response(String message, int status, Object content,int noofrows) {
		this.message = message;
		this.setStatus(status);
		this.content = content;
		this.setNoofrows(noofrows);
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
	
	public int getNoofrows() {
		return noofrows;
	}

	public void setNoofrows(int noofrows) {
		this.noofrows = noofrows;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  new Gson().toJson(this);
	}
	
}
