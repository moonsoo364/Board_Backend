package com.cos.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//http:/localhost:8000/api/hello
@RestController
@RequestMapping("/api")//url을 컨트롤러 메서드와 매핑할 때 사용한다.
public class HelloCotroller {
	
	@GetMapping("/hello")
	public String helloworld() {
		return "hello!";
	}

}
