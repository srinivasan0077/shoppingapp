package com.shoppingapp.entities;

import java.util.List;

public class Mail {

	private List<String> to;
	private String subject;
	private String message;
	
	public Mail(List<String> to, String subject, String message) {
		
		this.to = to;
		this.subject = subject;
		this.message = message;
	}

	
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
