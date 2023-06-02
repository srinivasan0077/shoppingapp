package com.shoppingapp.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.shoppingapp.authUtils.AuthUtilInterface;
import com.shoppingapp.authUtils.AuthUtilValidator;
import com.shoppingapp.entities.Mail;
import com.shoppingapp.entities.OTPHolder;
import com.shoppingapp.entities.Operations.Operation;
import com.shoppingapp.entities.User;

import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.DateManipulatorUtil;
import com.shoppingapp.utils.MailServiceUtil;
import com.shoppingapp.utils.RandomTokenGenerator;
import com.shoppingapp.utils.ResponseUtil;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000"},allowCredentials = "true")
@Controller
public class AuthController {
	
	@RequestMapping(path={"signup"},method=RequestMethod.GET)
	public String getSignUp() {
		
		return "signup";
	}
	
	@RequestMapping(path={"login"},method=RequestMethod.GET)
	public String getLoginPage() {
		return "login";
	}
	
	@RequestMapping(path={"logout"},method=RequestMethod.GET)
	public ResponseEntity<JSONObject> logoutUser(HttpSession session) {
		session.invalidate();
		return new ResponseEntity<JSONObject>(ResponseUtil.buildSuccessResponse("Successfully Logged Out"),HttpStatus.OK);
	}
	
	@RequestMapping(path={"login"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> authenticateUser(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse(error.getDefaultMessage()),HttpStatus.BAD_REQUEST);
		}
		
		if(user.getEmail()==null && user.getPassword()==null) {
			return  new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("Mandatory Fields cannot be null!"),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {

			String result=util.authenticate(user);
			if(result.equals(AuthUtilInterface.SUCCESS)) {
				String csrfToken=RandomTokenGenerator.getAlphaNumericString(40);
				User authUser=util.getUserBy("email",user.getEmail());
				authUser.setAuthenticated(true);
				session.setAttribute("user", authUser);
				session.setAttribute("csrfToken", csrfToken);
				util.closeConnection();
				JSONObject response=ResponseUtil.buildSuccessResponse("Successfully Logged In");
				response.put("csrfToken", csrfToken);
				response.put("userid",authUser.getUserid());
				response.put("username", authUser.getFirstname());
				response.put("roleid",authUser.getRoleid());
				return new ResponseEntity<JSONObject>(response,HttpStatus.OK);
			}else {
				return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("Authentication Failed"),HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			util.closeConnection();
		}
	  
		return new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse("Failed!"),HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	
	@RequestMapping(path={"otpforpwchange"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> sendOTPForMailId(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
		
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse(error.getDefaultMessage()),HttpStatus.BAD_REQUEST);
		}
		
		if(user.getEmail()==null) {
			return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("Email cannot be null"),HttpStatus.BAD_REQUEST);
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
					return new ResponseEntity<JSONObject>(ResponseUtil.buildSuccessResponse("OTP Sent Successfully!"),HttpStatus.OK);
				}else {
					session.invalidate();
				}
				
			}else {
				return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("User with this email not exist!"),HttpStatus.BAD_REQUEST);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			util.closeConnection();
		}
		 
		return new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse("Failed to send otp try again!"),HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@RequestMapping(path={"otpvalidationforpwchange"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> validateOTPForPWChange(HttpSession session,@RequestBody OTPHolder holder) {

		try {
		 
			String otp=holder.getOtp();
			OTPHolder otpHolder=(OTPHolder)session.getAttribute("otp");
			if( otpHolder!=null && otpHolder.getExpiry().after(new Date())) {
				if(otpHolder.getOtp().equals(otp)) {
					session.removeAttribute("otp");
					session.setAttribute("operation", Operation.ChangePassword);
					session.setAttribute("expiry", new DateManipulatorUtil().addMinutes(new Date(),3));
					return new ResponseEntity<JSONObject>(ResponseUtil.buildSuccessResponse("success"),HttpStatus.OK);
				}else {
					return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("Invalid OTP!"),HttpStatus.BAD_REQUEST);
				}
			}else {
				session.invalidate();
				return new ResponseEntity<JSONObject>(ResponseUtil.buildExpiryResponse("Time Expired"),HttpStatus.BAD_REQUEST);
			}
		 } catch (Exception e) { 
			 e.printStackTrace();
		 }
		
		return new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse("Failed!"),HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@RequestMapping(path={"changepw"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> changePassword(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse(error.getDefaultMessage()),HttpStatus.BAD_REQUEST);
		}
		
		if(user.getPassword()==null) {
			return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("Password cannot be null"),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		
		try {
			Date date=(Date)session.getAttribute("expiry");
			if(date!=null && date.after(new Date())) {
				User authuser=util.getUserBy("email",(String)session.getAttribute("email"));
				authuser.setPassword(user.getPassword());
				String result=util.updateUser(authuser);
				if(result.equals(AuthUtilInterface.SUCCESS)) {
					session.invalidate();
					return new ResponseEntity<JSONObject>(ResponseUtil.buildSuccessResponse("success"),HttpStatus.OK);
				}
				
			}else {
				session.invalidate();
				return new ResponseEntity<JSONObject>(ResponseUtil.buildExpiryResponse("Time to update Password Expired"),HttpStatus.BAD_REQUEST);
			}
		 } catch (Exception e) { 
			  e.printStackTrace();
		 }finally {
			 util.closeConnection();
		 }
		
		return new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse("Operation to change password failed!"),HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@RequestMapping(path={"checkUser"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<JSONObject> checkUserLogin(HttpSession session) {
		User user=(User)session.getAttribute("user");
		if(user != null && user.isAuthenticated()) {
			JSONObject responseJSON=ResponseUtil.buildAlreadyLoggedInResponse("User Logged In");
			responseJSON.put("csrfToken", (String)session.getAttribute("csrfToken"));
			responseJSON.put("userid",user.getUserid());
			responseJSON.put("username", user.getFirstname());
			responseJSON.put("roleid",user.getRoleid());
			return new ResponseEntity<JSONObject>(responseJSON,HttpStatus.OK);
		}
		
		return new ResponseEntity<JSONObject>(ResponseUtil.buildUnauthorizedResponse("Unauthenticated"),HttpStatus.OK);
	}
	
	
	
	@RequestMapping(path={"signup"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> postSignUp(HttpSession session,@Valid @RequestBody User user,BindingResult validationResult) {
	
		try {
			if(validationResult.hasErrors()) {
				FieldError error=validationResult.getFieldError();
				return  new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse(error.getDefaultMessage()),HttpStatus.BAD_REQUEST);
			}
			
			if(AuthUtilValidator.validateAndPreprocessUser(user)) {
				String otp=RandomTokenGenerator.getOTPString(6);
				OTPHolder otpHolder=new OTPHolder(otp,new DateManipulatorUtil().addMinutes(new Date(),3));
				user.setVerified(false);//should be removed proper testing
				user.setAuthenticated(false);//should be removed proper testing
				session.setAttribute("user",user);
				session.setAttribute("otp",otpHolder);
				session.setMaxInactiveInterval(4*60);
				session.setAttribute("operation", Operation.EmailValidation);
	            List<String> tos=new ArrayList<String>();
	            tos.add(user.getEmail());
	           // SessionInvalidatorScheduler scheduler=new SessionInvalidatorScheduler(user.getEmail());
	           // boolean result=scheduler.submitJob(session.getId());
	            // SessionInfoHolder.putSession(session.getId(), session);
				if(MailServiceUtil.send(new Mail(tos,"Otp to verify email", otp))) {
					return new ResponseEntity<JSONObject>(ResponseUtil.buildSuccessResponse("success"),HttpStatus.OK);
				}else {
					session.invalidate();
				}
			}else {
				return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("Mandatory fields cannot be null"),HttpStatus.BAD_REQUEST);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		return new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse("User Creation Failed!"),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(path={"resendcode"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<JSONObject> resendSignUpCode(HttpSession session) {
	
		try {
			User user=(User)session.getAttribute("user");
			Operation op=(Operation)session.getAttribute("operation");
			if(user!=null && op!=null && op.equals(Operation.EmailValidation)) {
				String otp=RandomTokenGenerator.getOTPString(6);
				OTPHolder otpHolder=new OTPHolder(otp,new DateManipulatorUtil().addMinutes(new Date(),3));
				session.setAttribute("otp",otpHolder);
	            List<String> tos=new ArrayList<String>();
	            tos.add(user.getEmail());
				if(MailServiceUtil.send(new Mail(tos,"Otp to verify email", otp))) {
					return new ResponseEntity<JSONObject>(ResponseUtil.buildSuccessResponse("success"),HttpStatus.OK);
				}
			}else {
				return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("User not signed up"),HttpStatus.BAD_REQUEST);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		return new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse("failed!"),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(path={"otpvalidation"},method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> validateOTP(HttpSession session,@Valid @RequestBody OTPHolder holder,BindingResult validationResult) {

		if(validationResult.hasErrors()) {
			FieldError error=validationResult.getFieldError();
			return  new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse(error.getDefaultMessage()),HttpStatus.BAD_REQUEST);
		}
		
		AuthUtilInterface util=(AuthUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("authutil");
		try {
		
			String otp=holder.getOtp();
			OTPHolder otpHolder=(OTPHolder)session.getAttribute("otp");
			if(otpHolder!=null && otpHolder.getExpiry().after(new Date())) {
				if(otpHolder.getOtp().equals(otp)) {
					String response=util.createUser((User)session.getAttribute("user"));
					session.invalidate();
					if(response.equals(AuthUtilInterface.CREATED)) {
						return new ResponseEntity<JSONObject>(ResponseUtil.buildSuccessResponse("success"),HttpStatus.OK);
					}else if(response.equals(AuthUtilInterface.EXIST)) {
						return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("User already Exist"),HttpStatus.BAD_REQUEST);
					}
				}else {
					return new ResponseEntity<JSONObject>(ResponseUtil.buildBadRequestResponse("Invalid OTP"),HttpStatus.BAD_REQUEST);
				}
			}else {
				session.invalidate();
				return new ResponseEntity<JSONObject>(ResponseUtil.buildExpiryResponse("Time Expired"),HttpStatus.BAD_REQUEST);
			}
		 } catch (Exception e) { 
			  e.printStackTrace();
		 }finally {
				util.closeConnection();
		 }
		
		return new ResponseEntity<JSONObject>(ResponseUtil.buildFailedResponse("User Creation failed"),HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
}
