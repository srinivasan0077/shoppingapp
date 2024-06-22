package com.shoppingapp.filters;

import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.User;
import com.shoppingapp.utils.ThreadLocalUtil;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AuthFilter extends HttpFilter implements Filter {
       
	private static final Logger logger=LogManager.getLogger(AuthFilter.class);
	private static final long serialVersionUID = 1L;
	private static String jspRegex="[A-Za-z]*\\.jsp";
	private static String localReactServer="http://localhost:3000";
	private static String remoteReactServer="https://www.royall.in";

	public AuthFilter() {
        super();
       
    }

	
	public void destroy() {
		
	}
	
	private boolean validateAuthHttpRequests(HttpServletRequest req,HttpServletResponse res) {
		 HttpSession session=req.getSession();
		 User user=(User)session.getAttribute("user");
		 String csrfToken=(String)session.getAttribute("csrfToken");
		 String csrfHeader=req.getHeader("csrfToken");
		 if(user == null || !user.getIsAuthenticated() || csrfToken==null) {
			 return false;
		 }
		 
		 if(csrfHeader==null || !csrfHeader.equals(csrfToken)) {
			 return false;
		 }
		 
		 return true;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		HttpSession session=req.getSession();
		String[] uri=req.getRequestURI().split("/");
		
		String logInfo="In Auth Filter Request URI:["
		+req.getRequestURI()+"] Remote IP:["+req.getRemoteAddr()+"]";
		logger.log(Level.INFO,logInfo);
		
		//setting logged in user to threadlocal
		User user=(User)session.getAttribute("user");
		if(user!=null && user.getIsAuthenticated()) {
			logger.log(Level.INFO,"User set in thread local :[userid"+user.getUserid()+"] [user email:"+user.getEmail()+"]");
			ThreadLocalUtil.currentUser.set(user);
		}
		
		if(uri.length>0 && !"OPTIONS".equals(req.getMethod())) {
			
			if(Pattern.matches(jspRegex,uri[uri.length-1])) {
				res.setStatus(404);
				setAllowOrgin(req, res);
				res.addHeader("Access-Control-Allow-Credentials", "true");
				Response responseJSON=new Response("Requested url not found!",Response.NOT_FOUND, null);
				res.getWriter().print(responseJSON.toString());
				return;
			}
			
			if(uri.length>=2) {
				if(Pattern.matches("login|signup",uri[1])) {
					if(user != null && user.getIsAuthenticated()) {
						res.setStatus(200);
						setAllowOrgin(req, res);
						res.addHeader("Access-Control-Allow-Credentials", "true");
						Response responseJSON=new Response("Successfully Logged In!",Response.SUCCESS, null);
						res.getWriter().print(responseJSON.toString());
						return;
					}
				}
				
				if(uri[1].equals("logout") || uri[1].equals("auth")) {
					if(!validateAuthHttpRequests(req, res)) {
						res.setStatus(401);
						setAllowOrgin(req, res);
						res.addHeader("Access-Control-Allow-Credentials", "true");
						Response responseJSON=new Response("Unauthorized!",Response.UNAUTHORIZED, null);
						res.getWriter().print(responseJSON.toString());
						return;
					}
				}
				
			}
			
		}
		
	    
		chain.doFilter(request, response);
		
		if(ThreadLocalUtil.currentUser.get()!=null) {
			ThreadLocalUtil.currentUser.remove();
		}

	}

	
	private void setAllowOrgin(HttpServletRequest req,HttpServletResponse res) {
		if (localReactServer.equals(req.getHeader("Origin"))) {
	        res.setHeader("Access-Control-Allow-Origin",localReactServer);
	    } else{
	        res.setHeader("Access-Control-Allow-Origin",remoteReactServer);
	    }
	}

}
