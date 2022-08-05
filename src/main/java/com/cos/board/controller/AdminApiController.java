package com.cos.board.controller;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.board.config.XssUtil;
import com.cos.board.dto.BoardDto;

import com.cos.board.dto.TokenDto;
import com.cos.board.dto.UserDto;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.model.Board;
import com.cos.board.model.RoleType;
import com.cos.board.model.User;
import com.cos.board.repository.UserRepository;
import com.cos.board.service.BoardService;
import com.cos.board.service.UserService;

@RestController
@RequestMapping(value="/api/admin")
public class AdminApiController {

	@Autowired
	private UserService userService;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private BoardService boardService;
	
	@PostMapping("/userInfo")
	public ResponseEntity<ArrayList<UserDto>> userInfo(@RequestHeader String Authorization) {

		if(jwtTokenProvider.isAdmin(Authorization)) {
			System.out.println(userService.selectAllUser());
			return new ResponseEntity(userService.selectAllUser(),HttpStatus.OK);
		} else {
			 return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping("/userUpdate")
	public ResponseEntity updateUser(@RequestHeader String Authorization,@RequestBody User user) {
		System.out.println(user);
		if(jwtTokenProvider.isAdmin(Authorization)) {
			//RoleType == ADMIN
			try {
				//update되면 200
				userService.updateUser(user.getId(), user.getEmail(), user.getPassword());
				return new ResponseEntity(HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		} else {
			 return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/existedPassword")
	public ResponseEntity<Boolean> existedUser(@RequestHeader String Authorization,@RequestBody User user) {
		System.out.println(Authorization);
		
		if(jwtTokenProvider.isAdmin(Authorization)) {
			boolean result=userService.isCorrectPw(user.getPassword(), user.getId());
			if(result) {
				return new ResponseEntity(result,HttpStatus.OK);
			}else {
				return new ResponseEntity(result,HttpStatus.OK);
			}
			
		} else {
			 return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping("/writingUser")
	public ResponseEntity<List<BoardDto>> writingUser(@RequestHeader String Authorization,@RequestBody User user) {
		
		if(jwtTokenProvider.isAdmin(Authorization)) {
			
			return new ResponseEntity(userService.selectUserWriting(user.getId()),HttpStatus.OK);
		} else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
			
		
	
		
	}
	
