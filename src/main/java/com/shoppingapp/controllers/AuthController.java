package com.shoppingapp.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.shoppingapp.authUtils.AuthUtilInterface;
import com.shoppingapp.authUtils.AuthUtilValidator;
import com.shoppingapp.entities.Mail;
import com.shoppingapp.entities.OTPHolder;
import com.shoppingapp.entities.Operations.Operation;
import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.User;

import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.DateManipulatorUtil;
import com.shoppingapp.utils.ExceptionCause;
import com.shoppingapp.utils.MailServiceUtil;
import com.shoppingapp.utils.RandomTokenGenerator;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000"},allowCredentials = "true")
@Controller
public class AuthController {
	
	private static final Logger logger=LogManager.getLogger(AuthController.class);
	
	@RequestMapping(path={"signup"},method=RequestMethod.GET)
	public String getSignUp() {
		
		return "signup";
	}
	
	@RequestMapping(path={"login"},method=RequestMethod.GET)
	public String getLoginPage() {
		return "login";
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
		
		if(user.getEmail()==null && user.getPassword()==null) {
			return new ResponseEntity<Response>(new Response("Invalid Email or Password!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {

            util.authenticate(user);
			String csrfToken=RandomTokenGenerator.getAlphaNumericString(40);
			User authUser=util.getUserBy("email",user.getEmail());
			authUser.setAuthenticated(true);
			session.setAttribute("user", authUser);
			session.setAttribute("csrfToken", csrfToken);
			util.closeConnection();
			JsonObject response=new JsonObject();
			response.addProperty("csrfToken", csrfToken);
			response.addProperty("userid",authUser.getUserid());
			response.addProperty("username", authUser.getFirstname());
			response.addProperty("roleid",authUser.getRoleid());
			return new ResponseEntity<Response>(new Response("Authentication Successfull!",Response.SUCCESS, response),HttpStatus.OK);
			
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
	
	@RequestMapping(path={"otpforpwchange"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> sendOTPForMailId(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		if(user.getEmail()==null) {
			return new ResponseEntity<Response>(new Response("Invalid Email!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {
			
			String result=util.checkUser(user.getEmail());
			if(result.equals(AuthUtilInterface.EXIST)) {
				List<String> tos=new ArrayList<String>();
		        tos.add(user.getEmail());
		        String otp=RandomTokenGenerator.getOTPString(6);
				OTPHolder otpHolder=new OTPHolder(otp,new DateManipulatorUtil().addMinutes(new Date(),3));
				session.setAttribute("email",user.getEmail());
				session.setAttribute("otp",otpHolder);
				session.setAttribute("operation", Operation.ValidationForPasswordChange);
				
		        if(MailServiceUtil.send(new Mail(tos,"Otp to verify email", otp))) {
		        	return new ResponseEntity<Response>(new Response("Otp sent successfully!",Response.SUCCESS, null),HttpStatus.OK);
				}else {
					session.invalidate();
					throw new ExceptionCause("Operation to send otp for password change failed!",Response.INTERNAL_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
			}else {
				return new ResponseEntity<Response>(new Response("Email not exist!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
			}
			
		}catch (ExceptionCause e) {
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
	
	@RequestMapping(path={"otpvalidationforpwchange"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> validateOTPForPWChange(HttpSession session,@RequestBody OTPHolder holder) {

		try {
		 
			String otp=holder.getOtp();
			OTPHolder otpHolder=(OTPHolder)session.getAttribute("otp");
			if( otpHolder!=null && otpHolder.getExpiry().after(new Date())) {
				if(otpHolder.getOtp().equals(otp)) {
					session.removeAttribute("otp");
					session.setAttribute("operation", Operation.ChangePassword);
					session.setAttribute("expiry", new DateManipulatorUtil().addMinutes(new Date(),3));
					return new ResponseEntity<Response>(new Response("Otp verification successfull!",Response.SUCCESS, null),HttpStatus.OK);
				}else {
					return new ResponseEntity<Response>(new Response("Invalid otp!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
				}
			}else {
				return new ResponseEntity<Response>(new Response("Otp expired!",Response.EXPIRY, null),HttpStatus.BAD_REQUEST);
			}
		 } catch (Exception e) { 
			    logger.log(Level.ERROR,e.getMessage());
			    logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
				return new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		 }
		
	}
	
	@RequestMapping(path={"changepw"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> changePassword(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		if(user.getPassword()==null) {
			return new ResponseEntity<Response>(new Response("Invalid password!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		
		try {
			Date date=(Date)session.getAttribute("expiry");
			if(date!=null && date.after(new Date())) {
				User authuser=util.getUserBy("email",(String)session.getAttribute("email"));
				authuser.setPassword(user.getPassword());
				util.updateUser(authuser);	
				session.invalidate();
				return new ResponseEntity<Response>(new Response("Password updated successfully!",Response.SUCCESS, null),HttpStatus.OK);	
			}else {
				session.invalidate();
				return new ResponseEntity<Response>(new Response("Time to change password expired!",Response.EXPIRY, null),HttpStatus.BAD_REQUEST);
			}
		 } catch (Exception e) { 
			 logger.log(Level.ERROR,e.getMessage());
			 logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
			 return new ResponseEntity<Response>(new Response("Internal Server Error!",Response.INTERNAL_ERROR,null),HttpStatus.INTERNAL_SERVER_ERROR);
		 }finally {
			 util.closeConnection();
		 }

		
	}
	
	@RequestMapping(path={"checkUser"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> checkUserLogin(HttpSession session) {
		User user=(User)session.getAttribute("user");
		if(user != null && user.isAuthenticated()) {
			JsonObject response=new JsonObject();
			response.addProperty("csrfToken", (String)session.getAttribute("csrfToken"));
			response.addProperty("userid",user.getUserid());
			response.addProperty("username", user.getFirstname());
			response.addProperty("roleid",user.getRoleid());
			return new ResponseEntity<Response>(new Response("User Logged in!!",Response.ACCEPTED, response),HttpStatus.OK);
		}
		
		return new ResponseEntity<Response>(new Response("User not logged in!",Response.UNAUTHORIZED, null),HttpStatus.UNAUTHORIZED);
	}
	
	
	
	@RequestMapping(path={"signup"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> postSignUp(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
	
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return new ResponseEntity<Response>(new Response(error.getDefaultMessage(),Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
		}
		
		try {
			
			    AuthUtilValidator.validateAndPreprocessUser(user);
				String otp=RandomTokenGenerator.getOTPString(6);
				OTPHolder otpHolder=new OTPHolder(otp,new DateManipulatorUtil().addMinutes(new Date(),3));
				user.setAuthenticated(false);//should be removed proper testing
				session.setAttribute("newuser",user);
				session.setAttribute("otp",otpHolder);
				session.setMaxInactiveInterval(4*60);
				session.setAttribute("operation", Operation.EmailValidation);
	            List<String> tos=new ArrayList<String>();
	            tos.add(user.getEmail());
	           // SessionInvalidatorScheduler scheduler=new SessionInvalidatorScheduler(user.getEmail());
	           // boolean result=scheduler.submitJob(session.getId());
	            // SessionInfoHolder.putSession(session.getId(), session);
				if(MailServiceUtil.send(new Mail(tos,"Otp to verify email", otp))) {
					return new ResponseEntity<Response>(new Response("Otp to verify account sent successfully!",Response.SUCCESS, null),HttpStatus.OK);
				}else {
					session.invalidate();
					throw new ExceptionCause("Operation to send otp to validate user failed!",Response.INTERNAL_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
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
	
	@RequestMapping(path={"resendcode"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> resendSignUpCode(HttpSession session) {
	
		try {
			User user=(User)session.getAttribute("newuser");
			Operation op=(Operation)session.getAttribute("operation");
			if(user!=null && op!=null && op.equals(Operation.EmailValidation)) {
				String otp=RandomTokenGenerator.getOTPString(6);
				OTPHolder otpHolder=new OTPHolder(otp,new DateManipulatorUtil().addMinutes(new Date(),3));
				session.setAttribute("otp",otpHolder);
	            List<String> tos=new ArrayList<String>();
	            tos.add(user.getEmail());
				if(MailServiceUtil.send(new Mail(tos,"Otp to verify email", otp))) {
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
			if(otpHolder!=null && otpHolder.getExpiry().after(new Date())) {
				if(otpHolder.getOtp().equals(otp)) {
					String response=util.createUser((User)session.getAttribute("newuser"));
					session.invalidate();
					if(response.equals(AuthUtilInterface.CREATED)) {
						return new ResponseEntity<Response>(new Response("User created successfully!",Response.SUCCESS, null),HttpStatus.OK);
					}else if(response.equals(AuthUtilInterface.EXIST)) {
						return new ResponseEntity<Response>(new Response("User alreay exist!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
					}else {
						throw new ExceptionCause("User Creation Failed!",Response.INTERNAL_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}else {
					return new ResponseEntity<Response>(new Response("Invalid Otp!",Response.BAD_REQUEST, null),HttpStatus.BAD_REQUEST);
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
