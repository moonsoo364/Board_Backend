package com.cos.board.jwt;

import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req =(HttpServletRequest)request;
		HttpServletResponse res =(HttpServletResponse)response;
		
		try{
//			String token= req.getHeader("Authrozation");
//			System.out.println("[MyFilter] token :"+token);
			System.out.println("[MyFilter] req.getRequestURL() :"+req.getRequestURL());
			System.out.println("[MyFilter] req.getHeader(\"Authorization\") :"+req.getHeader("Authorization"));
		}catch (Exception e){
			System.out.println(e);
		}
			chain.doFilter(request, response);
	}
}
