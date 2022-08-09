package com.cos.board.controller;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.board.config.XssUtil;
import com.cos.board.dto.BoardDto;

import com.cos.board.dto.TokenDto;
import com.cos.board.dto.UserDto;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.model.Board;
import com.cos.board.model.RoleType;
import com.cos.board.model.User;
import com.cos.board.repository.UserRepository;
import com.cos.board.service.BoardService;
import com.cos.board.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
@RequestMapping(value="/api/admin")
@Slf4j
public class AdminApiController {

	@Autowired
	private UserService userService;
	@Autowired
	private JwtInfo jwtInfo;
	@Autowired
	private BoardService boardService;
	
	@PostMapping("/all_user_infomation")
	public ResponseEntity<ArrayList<UserDto>> userInfo(@RequestHeader String Authorization) {

		if(jwtInfo.isAdmin(Authorization)) {
			System.out.println(userService.selectAllUser());
			return new ResponseEntity(userService.selectAllUser(),HttpStatus.OK);
		} else {
			 return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping("/user_update")
	public ResponseEntity updateUser(@RequestHeader String Authorization,@RequestBody User user) {
		System.out.println(user);
		if(jwtInfo.isAdmin(Authorization)) {
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
	@PostMapping("/password_existing")
	public ResponseEntity<Boolean> existedUser(@RequestHeader String Authorization,@RequestBody User user) {
		log.info("[existedUser] user 조회중");
		log.info("[existedUser] user : {}",user);
		if(jwtInfo.isAdmin(Authorization)) {
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
	
	
	@PostMapping("/user_writing")
	public ResponseEntity<List<BoardDto>> writingUser(@RequestHeader String Authorization,@RequestBody User user) {
		
		if(jwtInfo.isAdmin(Authorization)) {
			
			return new ResponseEntity(userService.selectUserWriting(user.getId()),HttpStatus.OK);
		} else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
		
	}
	
