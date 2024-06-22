package com.shoppingapp.controllers;

import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shoppingapp.authUtils.AuthUtilInterface;
import com.shoppingapp.entities.OTPHolder;
import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.User;
import com.shoppingapp.utils.AWSSimpleEmailService;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.DateManipulatorUtil;
import com.shoppingapp.utils.ExceptionCause;
import com.shoppingapp.utils.GoogleAuthenticator;
import com.shoppingapp.utils.HtmlTemplates;
import com.shoppingapp.utils.RandomTokenGenerator;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000","https://www.royall.in","https://royall.in"},allowCredentials = "true")
@Controller
public class AuthController {
	
	private static final Logger logger=LogManager.getLogger(AuthController.class);
	
	
	@RequestMapping(path={"/"},method=RequestMethod.GET)
	public ResponseEntity<Response> checkServerHealth() {
		return new ResponseEntity<Response>(new Response("Server Health is good!",Response.SUCCESS, null),HttpStatus.OK);
	}
	
	@RequestMapping(path={"logout"},method=RequestMethod.GET)
	public ResponseEntity<Response> logoutUser(HttpSession session) {
		session.invalidate();
		return new ResponseEntity<Response>(new Response("Successfully logged out!",Response.SUCCESS, null),HttpStatus.OK);
	}
	
	@RequestMapping(path={"login"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> authenticateUser(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		if(user.getEmail()==null) {
			return new ResponseEntity<Response>(new Response("Invalid Email or Phone Number!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {

			
			String otp=RandomTokenGenerator.getOTPString(6);
			OTPHolder otpHolder=new OTPHolder(otp,DateManipulatorUtil.addMinutes(new Date(),3));
			session.setAttribute("newuser",user);
			session.setAttribute("otp",otpHolder);
			if(AWSSimpleEmailService.sendEmail(user.getEmail(),"Otp to verify user!",HtmlTemplates.getSignupOtpTemplate(user.getEmail(), otp))) {
				return new ResponseEntity<Response>(new Response("Otp to verify account sent successfully!",Response.SUCCESS, null),HttpStatus.OK);
			}else {
				session.invalidate();
				throw new ExceptionCause("Operation to send otp to validate user failed!",Response.INTERNAL_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
			}

			
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR,e.getMessage());
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR,e.getMessage());
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
	
	}
	
	@RequestMapping(path={"google/login"},method=RequestMethod.POST,consumes = {"multipart/form-data"})
	@ResponseBody
	public ResponseEntity<Response> googleLogin(HttpSession session,@RequestParam("jwtcredential") String jwtcredential) {
		
		
		if(jwtcredential==null) {
			return new ResponseEntity<Response>(new Response("Invalid Google Auth Credential.Try Again!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=null;
		try {
            User googleUser=GoogleAuthenticator.verifyAndGetUser(jwtcredential);
			util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
			String response=util.createVerifiedUser(googleUser);
			
			if(response.equals(AuthUtilInterface.CREATED)) {		
				User authUser=util.getUserBy("email",googleUser.getEmail());
				String csrfToken=RandomTokenGenerator.getAlphaNumericString(40);	
				authUser.setIsAuthenticated(true);
				session.setAttribute("user", authUser);
				session.setAttribute("csrfToken", csrfToken);
				
				JSONObject httpresponse=new JSONObject();
				httpresponse.put("csrfToken", csrfToken);
				httpresponse.put("userid",authUser.getUserid());
				httpresponse.put("username", authUser.getFirstname());
				httpresponse.put("roleid",authUser.getRoleid());
				return new ResponseEntity<Response>(new Response("Authentication Successfull!",Response.SUCCESS, httpresponse.toString()),HttpStatus.OK);
			}else {
				session.invalidate();
				throw new ExceptionCause("Google Authentication Failed!",Response.INTERNAL_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
			}

			
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR,e.getMessage());
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR,e.getMessage());
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response("Google Authentication Failed!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			if(util!=null) {
			   util.closeConnection();
			}
		}
	
	}
	

	
	@RequestMapping(path={"auth/api/account"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getAccount() {
		ResponseEntity<Response> response;
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,util.getAccountOfUser()),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"auth/api/account/edit"},method=RequestMethod.POST,consumes = {"multipart/form-data"})
	@ResponseBody
	public ResponseEntity<Response> editAccount(@RequestParam(value="fieldName",required = false) String fieldName,@RequestParam(value="fieldValue",required = false) String fieldValue) {
		ResponseEntity<Response> response;
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {
			util.updateField(fieldName, fieldValue);
			response= new ResponseEntity<Response>(new Response("Operation Successful!",Response.SUCCESS,null),HttpStatus.OK);
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response=new ResponseEntity<Response>(new Response(e.getMessage(),Response.BAD_REQUEST,null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			response= new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			util.closeConnection();
		}
		
        return response;
	}
	
	@RequestMapping(path={"checkUser"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> checkUserLogin(HttpSession session) {
		User user=(User)session.getAttribute("user");
		if(user != null && user.getIsAuthenticated()) {
			JSONObject response=new JSONObject();
			response.put("csrfToken", (String)session.getAttribute("csrfToken"));
			response.put("userid",user.getUserid());
			response.put("username", user.getFirstname());
			response.put("roleid",user.getRoleid());
			return new ResponseEntity<Response>(new Response("User Logged in!!",Response.ACCEPTED, response.toString()),HttpStatus.OK);
		}
		
		return new ResponseEntity<Response>(new Response("User not logged in!",Response.UNAUTHORIZED, null),HttpStatus.UNAUTHORIZED);
	}
	
	
	
	@RequestMapping(path={"resendcode"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> resendSignUpCode(HttpSession session) {
	
		try {
			User user=(User)session.getAttribute("newuser");
			
			if(user!=null) {
				
				String otp=RandomTokenGenerator.getOTPString(6);
				OTPHolder otpHolder=new OTPHolder(otp,DateManipulatorUtil.addMinutes(new Date(),3));
				session.setAttribute("otp",otpHolder);

				if(AWSSimpleEmailService.sendEmail(user.getEmail(),"Otp to verify user!",HtmlTemplates.getSignupOtpTemplate(user.getEmail(), otp))) {
					return new ResponseEntity<Response>(new Response("Otp resent successfully!",Response.SUCCESS, null),HttpStatus.OK);
				}else {
					throw new ExceptionCause("Operation to resend otp to validate user failed!",Response.INTERNAL_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
				}

			}else {
				return new ResponseEntity<Response>(new Response("Unauthorized!",Response.UNAUTHORIZED, null),HttpStatus.BAD_REQUEST);
			}
			
		}catch (ExceptionCause e) {
			logger.log(Level.ERROR,e.getMessage());
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response(e.getMessage(),e.getStatus(),null),e.getErrorCode());
		}catch (Exception e) {
			logger.log(Level.ERROR,e.getMessage());
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			return new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@RequestMapping(path={"otpvalidation"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> validateOTP(HttpSession session,@Valid @RequestBody OTPHolder holder,BindingResult validationResult) {

		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {
		
			String otp=holder.getOtp();
			OTPHolder otpHolder=(OTPHolder)session.getAttribute("otp");
			User user=(User)session.getAttribute("newuser");
			if(otpHolder!=null && otpHolder.getExpiry().after(new Date())) {
				if(otpHolder.getOtp().equals(otp)) {
					String response=util.createVerifiedUser(user);
			
					if(response.equals(AuthUtilInterface.CREATED)) {		
						User authUser=util.getUserBy("email",user.getEmail());
						String csrfToken=RandomTokenGenerator.getAlphaNumericString(40);	
						authUser.setIsAuthenticated(true);
						session.setAttribute("user", authUser);
						session.setAttribute("csrfToken", csrfToken);
						
						JSONObject httpresponse=new JSONObject();
						httpresponse.put("csrfToken", csrfToken);
						httpresponse.put("userid",authUser.getUserid());
						httpresponse.put("username", authUser.getFirstname());
						httpresponse.put("roleid",authUser.getRoleid());
						return new ResponseEntity<Response>(new Response("Authentication Successfull!",Response.SUCCESS, httpresponse.toString()),HttpStatus.OK);
					}else {
						session.invalidate();
						throw new ExceptionCause("Authentication Failed!",Response.INTERNAL_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}else {
					return new ResponseEntity<Response>(new Response("Incorrect Otp!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
				}
			}else {
				return new ResponseEntity<Response>(new Response("Otp to verify user expired!",Response.EXPIRY, null),HttpStatus.BAD_REQUEST);
			}
		 } catch (ExceptionCause e) {
			    logger.log(Level.ERROR,e.getMessage());
				logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
				return new ResponseEntity<Response>(new Response(e.getMessage(),e.getStatus(),null),e.getErrorCode());
		}catch (Exception e) {
			    logger.log(Level.ERROR,e.getMessage());
				logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
				return new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
				util.closeConnection();
		 }
		
		
	}
	
}
