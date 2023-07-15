package com.shoppingapp.entities;

import javax.validation.constraints.Pattern;


public class User {

	private Long userid;
	
	@Pattern(regexp = "M|F",message = "Enter valid gender")
	private String gender;
	
	@Pattern(regexp = "[A-Za-z]{1,20}",message="Enter valid first name")
	private String firstname;
	
	@Pattern(regexp = "[A-Za-z]{1,20}",message="Enter valid last name")
	private String lastname;
	
	@Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",message="Enter valid email")
	private String email;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$",message="Enter valid password")
	private String password;
	
	@Pattern(regexp = "[0-9]{10}",message = "Enter valid phone number")
	private String phone;
	
	private String addressline1;
	private String addressline2;
	private String towncity;
	private String county;
	private String country;
	private Long roleid;
	private String salt;
	private boolean isAuthenticated=false;
	
	
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddressline1() {
		return addressline1;
	}
	public void setAddressline1(String addressline1) {
		this.addressline1 = addressline1;
	}
	public String getAddressline2() {
		return addressline2;
	}
	public void setAddressline2(String addressline2) {
		this.addressline2 = addressline2;
	}
	public String getTowncity() {
		return towncity;
	}
	public void setTowncity(String towncity) {
		this.towncity = towncity;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Long getRoleid() {
		return roleid;
	}
	public void setRoleid(Long roleid) {
		this.roleid = roleid;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
	
	@Override
	public String toString() {
		return "User [userid=" + userid + ", gender=" + gender + ", firstname=" + firstname + ", lastname=" + lastname
				+ ", email=" + email + ", password=" + password + ", phone=" + phone + ", addressline1=" + addressline1
				+ ", addressline2=" + addressline2 + ", towncity=" + towncity + ", county=" + county + ", country="
				+ country + ", roleid=" + roleid + "]";
	}

	
}