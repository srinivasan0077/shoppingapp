package com.shoppingapp.authUtils;



import com.shoppingapp.entities.User;
import com.shoppingapp.utils.BeanFactoryWrapper;

public class AuthUtilValidator {

    
	public static boolean validateAndPreprocessUser(User user) {
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		
		try {
			if(user.getFirstname()==null || user.getLastname()==null
					|| user.getEmail()==null || user.getPassword()==null
					) {
				return false;
			} 
			
			if(user.getRoleid()!=null || user.getUserid()!=null || user.isAuthenticated() || 
			   user.isVerified()) {
				return false;
			}
			
		    String response=util.checkUser(user.getEmail());
		    
		    if(response.equals(AuthUtilInterface.NOT_EXIST) || response.equals(AuthUtilInterface.NOT_VERIFIED)) {
		    	return true;
		    }
		    
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			util.closeConnection();
		}
		
	    return false;
	}
	
	
}
