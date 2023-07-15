package com.shoppingapp.filters;




import com.shoppingapp.entities.Operations;
import com.shoppingapp.entities.Operations.Operation;
import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.User;
import com.shoppingapp.utils.ThreadLocalUtil;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AuthFilter extends HttpFilter implements Filter {
       
    
	private static final long serialVersionUID = 1L;

	public AuthFilter() {
        super();
       
    }

	
	public void destroy() {
		
	}
	
	private boolean validateAuthHttpRequests(HttpServletRequest req,HttpServletResponse res) {
		 HttpSession session=req.getSession();
		 User user=(User)session.getAttribute("user");
		 String csrfToken=(String)session.getAttribute("csrfToken");
		 String csrfHeader=req.getHeader("csrf_token");
		 if(user == null || !user.isAuthenticated() || csrfToken==null) {
			 return false;
		 }
		 
		 if(csrfHeader==null || !csrfHeader.equals(csrfToken)) {
			 return false;
		 }
		 
		 return true;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("In Auth Filter");
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		HttpSession session=req.getSession();
		String[] uri=req.getRequestURI().split("/");
		String jspRegex="[A-Za-z]*\\.jsp";
		String pattern="otpvalidation|otpvalidationforpwchange|changepw";
		String authUrls="logout";
		System.out.println(req.getRequestURI());
		System.out.println(uri.length);
		System.out.println(req.getRemoteAddr());
		
		//setting logged in user to threadlocal
		User user=(User)session.getAttribute("user");
		if(user!=null && user.isAuthenticated()) {
			ThreadLocalUtil.currentUser.set(user);
		}
		
		if(uri.length>0 && !"OPTIONS".equals(req.getMethod())) {
			
			if(Pattern.matches(jspRegex,uri[uri.length-1])) {
				res.setStatus(404);
				res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
				res.addHeader("Access-Control-Allow-Credentials", "true");
				Response responseJSON=new Response("Requested url not found!",Response.NOT_FOUND, null);
				res.getWriter().print(responseJSON.toString());
				return;
			}
			
			if(uri.length>=3) {
				if(Pattern.matches("login|signup",uri[2])) {
					if(user != null && user.isAuthenticated()) {
						res.setStatus(200);
						res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
						res.addHeader("Access-Control-Allow-Credentials", "true");
						Response responseJSON=new Response("Successfully Logged In!",Response.SUCCESS, null);
						res.getWriter().print(responseJSON.toString());
						return;
					}
				}
				
				if(Pattern.matches(authUrls,uri[2] )) {
					if(!validateAuthHttpRequests(req, res)) {
						res.setStatus(401);
						res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
						res.addHeader("Access-Control-Allow-Credentials", "true");
						Response responseJSON=new Response("Unauthorized!",Response.UNAUTHORIZED, null);
						res.getWriter().print(responseJSON.toString());
						return;
					}
				}
				
				if(Pattern.matches(pattern,uri[2])) {
					Operation op=Operations.urlOpMap.get(uri[2]);
					if(!op.validate(session)) {
						res.setStatus(400);
						res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
						res.addHeader("Access-Control-Allow-Credentials", "true");
						res.getWriter().print(op.sendResponse(session).toString());
						return;
					}
				}
			}
			
		}
		
	    
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
