package com.cos.board.controller;


import java.util.ArrayList;


import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.board.config.XssUtil;
import com.cos.board.dto.BoardDto;
import com.cos.board.dto.LoginDto;
import com.cos.board.dto.SelectBoardDto;
import com.cos.board.dto.TokenDto;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.model.Board;
import com.cos.board.model.User;
import com.cos.board.repository.BoardRepository;
import com.cos.board.service.BoardService;
import com.cos.board.service.UserService;


@RestController
@RequestMapping("/api/noAuth")
public class NoAuthApiController {
	@Autowired
	private UserService userService;
	@Autowired
	private BoardService boardService;

	public NoAuthApiController(UserService userService) {
		this.userService =userService;
	}
	//*아이디확인
	@PostMapping("/checkName")
	public ResponseEntity checkName(@RequestBody User user){
		
		boolean noExisteId = userService.checkId(user.getUsername());
		System.out.println("noExisteId : "+noExisteId);
		if(noExisteId) {
			return new ResponseEntity(HttpStatus.OK);
		}else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	//*회원가입
	@PostMapping("/joinProc")
	public ResponseEntity save(@RequestBody User user){
		System.out.println(user);
		boolean joinUser=userService.register(user);
		
		if(joinUser) {
			System.out.println("SUCCESS Connect SERVER!");
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
		System.out.println("FAILED Connect SERVER!");
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/existUser")
	public ResponseEntity<LoginDto> existUser(@RequestBody User user) {			
		//로그인
		LoginDto loginDto =userService.checkUser(user,user.getPassword());
		HttpHeaders headers =new HttpHeaders();
		headers.add("Authorization", loginDto.getToken());
		if(loginDto.getCode()!=0) {
			return new ResponseEntity(loginDto,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(loginDto,headers,HttpStatus.OK);
			
	}
	
	@PostMapping("/getBoard")
	public ResponseEntity<ArrayList<SelectBoardDto>> getBoard() {
		//게시글 목록
		try {
		ArrayList<SelectBoardDto> boardList= boardService.getBoardData();
			return new ResponseEntity(boardList,HttpStatus.OK);
		}catch (Exception e){
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
		
}
