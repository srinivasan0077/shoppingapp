package com.shoppingapp.entities;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.shoppingapp.utils.ResponseUtil;



public class Operations {

	public static Map<String,Operation> urlOpMap=new HashMap<String,Operation>();
	
	static {
		urlOpMap.put("otpvalidation", Operation.EmailValidation);
		urlOpMap.put("otpvalidationforpwchange", Operation.ValidationForPasswordChange);
		urlOpMap.put("changepw", Operation.ChangePassword);
	}
	
	public enum Operation{
		
		EmailValidation{
			@Override
			public boolean validate(HttpSession session) {
				Operation op=(Operation)session.getAttribute("operation");
				if(op!=null && op.equals(EmailValidation)) {
					return true;
				}
				return false;
			}
			
			@Override
			public JSONObject sendResponse(HttpSession session) {
				return ResponseUtil.buildExpiryResponse("Otp Expired");
			}
			
			
		},ValidationForPasswordChange{
			
			@Override
			public boolean validate(HttpSession session) {
				Operation op=(Operation)session.getAttribute("operation");
				if(op!=null && op.equals(ValidationForPasswordChange)) {
					return true;
				}
				return false;
			}
			
			@Override
			public JSONObject sendResponse(HttpSession session) {
				
				return ResponseUtil.buildExpiryResponse("Otp Expired");
			}
		},ChangePassword{
			@Override
			public boolean validate(HttpSession session) {
				Operation op=(Operation)session.getAttribute("operation");
				if(op!=null && op.equals(ChangePassword)) {
					return true;
				}
				return false;
			}
			
			@Override
			public JSONObject sendResponse(HttpSession session) {
				return ResponseUtil.buildExpiryResponse("Time to change password has been expired");
			}
		};
		
		public boolean validate(HttpSession session) {
			return true;
		}
		
		public JSONObject sendResponse(HttpSession session) {
			return new JSONObject();
		}
	};
}
