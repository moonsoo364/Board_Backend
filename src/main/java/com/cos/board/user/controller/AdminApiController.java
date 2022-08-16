package com.cos.board.user.controller;



import java.util.ArrayList;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.board.board.dto.BoardDto;
import com.cos.board.board.model.Board;
import com.cos.board.board.service.BoardService;
import com.cos.board.config.XssUtil;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.dto.TokenDto;
import com.cos.board.user.dto.UserDto;
import com.cos.board.user.model.RoleType;
import com.cos.board.user.model.User;
import com.cos.board.user.repository.UserRepository;
import com.cos.board.user.service.AdminService;
import com.cos.board.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
//@CrossOrigin(origins = "http://localhost:3000/") cors 단독설정
@RequestMapping(value="/api/admin")
@Slf4j
public class AdminApiController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private JwtInfo jwtInfo;
	@Autowired
	private BoardService boardService;
	@GetMapping("/all_user_infomation")
	public ResponseEntity<ArrayList<UserDto>> userInfo(@RequestHeader String Authorization) {
		log.info("[userInfo] 사용자 정보 조회중");
		if(jwtInfo.isAdmin(Authorization)) {
			System.out.println(adminService.selectAllUser());
			return new ResponseEntity(adminService.selectAllUser(),HttpStatus.OK);
		} else {
			 return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PutMapping("/user_update")
	public ResponseEntity updateUser(@RequestHeader String Authorization,@RequestBody User user) {
		log.info("[updateUser] 사용자 정보 수정시작.");
		if(jwtInfo.isAdmin(Authorization)) {
			//RoleType == ADMIN
			try {
				//update되면 200
				adminService.updateUser(user.getId(), user.getEmail(), user.getPassword());
				return new ResponseEntity(HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		} else {
			 return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping("/password_existing")
	public ResponseEntity<Boolean> existedUser(@RequestHeader String Authorization,@RequestParam("password") String password,@RequestParam("id") int id) {
		log.info("[existedUser] user 조회중");
		log.info("[existedUser] user : {}",id);
		if(jwtInfo.isAdmin(Authorization)) {
			boolean result=adminService.isCorrectPw(password,id);
			if(result) {
				return new ResponseEntity(result,HttpStatus.OK);
			}else {
				return new ResponseEntity(result,HttpStatus.OK);
			}
			
		} else {
			 return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
	}
	
	
	@GetMapping("/user_writing")
	public ResponseEntity<List<BoardDto>> writingUser(@RequestHeader String Authorization,@RequestParam("id") int id) {
		
		if(jwtInfo.isAdmin(Authorization)) {
			if(adminService.selectUserWriting(id) != null) {
			return new ResponseEntity(adminService.selectUserWriting(id),HttpStatus.OK);
			}else {
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
	}
	
		
	}
	
