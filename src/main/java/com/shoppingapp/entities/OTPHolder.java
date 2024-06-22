package com.shoppingapp.entities;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

public class OTPHolder {

	@NotNull(message = "otp cannot be null")
	private String otp;
	
	@Null(message = "Cannot process invalid fields!")
	private Date expiry;
	
	public OTPHolder(String otp, Date expiry) {
		this.otp = otp;
		this.expiry = expiry;
	}
	
	public OTPHolder() {}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	
	
	
}
