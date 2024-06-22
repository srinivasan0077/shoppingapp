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
	
	public String createVerifiedUser(User user);
	
	public String checkUserBy(String columname,String value);
	
	public boolean isAdmin(Long id);
	
	public User getAccountOfUser() throws Exception;
	
	public void updateField(String fieldName,String fieldValue)throws Exception;
	
	public void closeConnection();
	
}
