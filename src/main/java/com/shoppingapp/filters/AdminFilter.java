package com.shoppingapp.filters;




import java.io.IOException;

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

import com.shoppingapp.entities.Response;
import com.shoppingapp.entities.User;
import com.shoppingapp.utils.ResponseUtil;







public class AdminFilter extends HttpFilter implements Filter  {
       
	private static final long serialVersionUID = 1L;

	public AdminFilter() {
        super();
    }

	
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("In Admin Filter");
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		HttpSession session=req.getSession();
		
		if("OPTIONS".equals(req.getMethod())) {
			chain.doFilter(request, response);
		}else {
			System.out.println(req.getHeader("csrf-token"));
			
			User user=(User)session.getAttribute("user");
			if(user==null || !user.isAuthenticated()) {
	            Response responseJSON=new Response("Unauthorized",Response.UNAUTHORIZED,null);
				res.setStatus(401);
				res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
				res.addHeader("Access-Control-Allow-Credentials", "true");
				res.getWriter().print(responseJSON.toString());
				return;
			}
			
			if(user.getRoleid()==null || user.getRoleid()!=Long.valueOf(2)) {
				Response responseJSON=new Response("Unauthorized",Response.UNAUTHORIZED,null);
				res.setStatus(401);
				res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
				res.addHeader("Access-Control-Allow-Credentials", "true");
				res.getWriter().print(responseJSON.toString());
				return;
			}
			
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
