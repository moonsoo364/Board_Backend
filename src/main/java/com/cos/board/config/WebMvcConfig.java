package com.cos.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	//전역 cors 설정
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		 registry.addMapping("/api/**")
		 .allowedOrigins("http://localhost:3000")
		 .allowedMethods("GET","POST","PUT","DELETE");
		 
//		 restful api
//		 
//		 rule : 
//			 
//			 crud : create, read, update, delete
//			 method : POST
//			 
//			 
//			 /api/board/insert : create
//			 /api/board/select : read
//			 /api/board/update : update
//			 /api/board/delete : delete
//			 
//			 
//			 Method :
//				 GET, POST, DELETE, PUT
//				 
//				 GET : /api/board/{id}
//				 POST : /api/board
//				 DELTE : /api/board
//				 PUT : /api/board
				 
	 }
}
