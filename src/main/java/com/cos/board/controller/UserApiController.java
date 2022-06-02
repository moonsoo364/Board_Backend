package com.cos.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.board.dto.ResponseDto;
import com.cos.board.model.User;
import com.cos.board.service.UserService;

@RestController
@RequestMapping("/api")
public class UserApiController {
	@Autowired
	private UserService userService;

	//http:/localhost:8000/api/auth/joinProc
	@PostMapping("auth/joinProc")
	public ResponseDto<Integer> save(@RequestBody User user){
		
		userService.register(user);
		return new ResponseDto<Integer>(HttpStatus.OK.value(),1);
		
	}
	
}
