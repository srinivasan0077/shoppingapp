package com.shoppingapp.utils;

import java.util.Collections;

import org.springframework.http.HttpStatus;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.shoppingapp.entities.User;


public class GoogleAuthenticator {

	private static String CLIENT_ID;
	
	static {
		CLIENT_ID=System.getProperty("google-client-id");
	}
	
	public static User verifyAndGetUser(String jwtcredential) throws Exception {
		 HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		 JsonFactory jsonFactory=JacksonFactory.getDefaultInstance();
	     
		 GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport,jsonFactory)
				    .setAudience(Collections.singletonList(CLIENT_ID))
				    .build();
		 GoogleIdToken idToken = verifier.verify(jwtcredential);
		 if (idToken != null) {
			  Payload payload = idToken.getPayload();
			  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			  
			  if(emailVerified) {			  
	              User user=new User();
				  user.setEmail(payload.getEmail());
				  return user;
			  }
			  
		  } 
		  throw new ExceptionCause("Google Authentication Failed!",HttpStatus.BAD_REQUEST);
		 
	}
	
}
