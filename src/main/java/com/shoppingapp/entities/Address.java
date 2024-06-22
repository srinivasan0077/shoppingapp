package com.shoppingapp.entities;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Address {

	private Long addressId;
	
	@Size(min = 1,max = 255,message = "Address Line 1 field length should be between 1 and 255!")
	@NotNull
	private String addressLine1;
	
	@Size(max = 255,message = "Address Line 2 field length cannot exceed 255!")
	private String addressLine2;
	
	@Size(min = 1,max = 50,message = "City field length should be between 1 and 50!")
	@NotNull
	private String city;
	
	@Size(min = 1,max = 50,message = "State field length should be between 1 and 50!")
	@NotNull
	private String state;
	
	@Pattern(regexp = "India",message="country field is not valid!")
	@NotNull
	private String country;
	
	@Pattern(regexp = "[0-9]{6}",message = "Enter valid postal code")
	@NotNull
	private String postalCode;
	
	@Pattern(regexp = "[0-9]{10}",message = "Enter valid mobile number")
	@NotNull
	private String mobile;
	
	@Null(message = "Invalid field [userId]!")
	private Long userId;
	
	private Integer totalValue;
	
	@NotNull
	private Boolean isCart;
	
	
	@Valid
	@NotNull
	private List<OrderItems> orderItems;

	
	public Long getAddressId() {
		return addressId;
	}
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public List<OrderItems> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItems> orderItems) {
		this.orderItems = orderItems;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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
	public Integer getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(Integer totalValue) {
		this.totalValue = totalValue;
	}
	public Boolean getIsCart() {
		return isCart;
	}
	public void setIsCart(Boolean isCart) {
		this.isCart = isCart;
	}
	
	
}
