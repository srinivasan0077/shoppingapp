package com.shoppingapp.authUtils;

import com.shoppingapp.entities.User;

public interface AuthUtilInterface {

	public String CREATED="CREATED";
	public String FAILED="FAILED";
	public String SUCCESS="SUCCESS";
	public String EXIST="EXIST";
	public String NOT_EXIST="NOT_EXIST";
	public String NOT_VERIFIED="NOT_VERIFIED";
	
	public User getUserBy(String key,Object value);
	
	public String createUser(User user);
	
	public String updateUser(User user);
	
	public String authenticate(User user);
	
	public String checkUser(String email);
	
	public boolean isAdmin(Long id);
	
	public void closeConnection();
	
}
