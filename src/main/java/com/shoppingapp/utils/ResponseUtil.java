package com.shoppingapp.utils;

import org.json.JSONObject;

public class ResponseUtil {

	public static JSONObject buildSuccessResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",2000);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildBadRequestResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",4000);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildUnauthorizedResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",4001);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildFailedResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",5000);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildAlreadyLoggedInResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",3000);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildExpiryResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",4002);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildAcceptedResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",2001);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildNotFoundResponse(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",4004);
		obj.put("message",body );
		return obj;
	}
	
	public static JSONObject buildInvalidCredentials(String body) {
		JSONObject obj=new JSONObject();
		obj.put("status",4010);
		obj.put("message",body );
		return obj;
	}
	
}
