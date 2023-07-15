package com.shoppingapp.authUtils;



import org.springframework.http.HttpStatus;

import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.User;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.ExceptionCause;

public class AuthUtilValidator {

    
	public static void validateAndPreprocessUser(User user) throws Exception{
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		
		
		if(user.getFirstname()==null || user.getLastname()==null
				|| user.getEmail()==null || user.getPassword()==null
				) {
			throw new ExceptionCause("Mandatory Fields cannot be null!",Response.BAD_REQUEST, HttpStatus.BAD_REQUEST);
		} 
		
		if(user.getRoleid()!=null || user.getUserid()!=null || user.isAuthenticated()) {
			throw new ExceptionCause("Cannot process invalid fields!",Response.BAD_REQUEST,HttpStatus.BAD_REQUEST);
		}
		
	    String response=util.checkUser(user.getEmail());
	    
	    if(response.equals(AuthUtilInterface.EXIST)) {
	    	throw new ExceptionCause("Email already used!",Response.BAD_REQUEST,HttpStatus.BAD_REQUEST);
	    }
		    
		
	}
	
	
}
