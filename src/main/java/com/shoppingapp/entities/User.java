package com.shoppingapp.entities;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class User {

	private Long userid;
	
	@Pattern(regexp = "M|F",message = "Enter valid gender")
	private String gender;
	
	@Size(min = 1,max = 20,message="Enter valid first name")
	private String firstname;
	
	@Size(min = 1,max = 20,message="Enter valid last name")
	private String lastname;
	
	@Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",message="Invalid email")
	@Size(max = 320,message = "Invalid email")
	private String email;

	@Size(min = 6,max = 20,message="Invalid password")
	private String password;
	
	@Pattern(regexp = "[0-9]{10}",message = "Enter valid phone number")
	private String phone;
	
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
	
	public boolean getIsAuthenticated() {
		return isAuthenticated;
	}
	public void setIsAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
	@Override
	public String toString() {
		return "User [userid=" + userid + ", gender=" + gender + ", firstname=" + firstname + ", lastname=" + lastname
				+ ", email=" + email + ", password=" + password + ", phone=" + phone + ", roleid=" + roleid + ", salt="
				+ salt + ", isAuthenticated=" + isAuthenticated + "]";
	}
	
	

	
}