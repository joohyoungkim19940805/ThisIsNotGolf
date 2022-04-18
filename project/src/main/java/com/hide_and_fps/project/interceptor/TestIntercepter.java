package com.hide_and_fps.project.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class TestIntercepter implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		System.out.println("preHandle<<<<<<");
		System.out.println(request.getRequestURI()); // == /page
		System.out.println(request.getRequestURL());
		System.out.println(request.getAttribute("test"));
		System.out.println(request.getMethod());
		System.out.println(request.getSession().getAttribute("user") != null);
		response.sendRedirect("/");
		if(request.getRequestURI().equals("/PrevTargetPage")) {
			response.setHeader("Expires", "-1"); 
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0"); 
			response.setHeader("Pragma", "no-cache");
			//response.sendRedirect("/");
		}
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		System.out.println("postHandle<<<");
		System.out.println(request.getRequestURL());
		
	}
}
