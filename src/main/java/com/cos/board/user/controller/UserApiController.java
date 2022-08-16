package com.cos.board.user.controller;


import java.nio.charset.Charset;

import java.util.ArrayList;


import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.board.board.dto.BoardDto;
import com.cos.board.board.dto.SelectBoardDto;
import com.cos.board.board.model.Board;
import com.cos.board.board.repository.BoardRepository;
import com.cos.board.board.service.BoardService;
import com.cos.board.config.XssUtil;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.jwt.dto.TokenDto;
import com.cos.board.user.dto.LoginDto;
import com.cos.board.user.model.User;
import com.cos.board.user.service.UserService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api")
@Slf4j
public class UserApiController {
	@Autowired
	private UserService userService;
	@Autowired
	private BoardService boardService;
	public UserApiController(UserService userService) {
		this.userService =userService;
	}
	
	//*아이디확인
	@GetMapping("/name_check")
	public ResponseEntity<Boolean> checkName(@RequestParam("username") String username){
		
		try {
			boolean noExisteId = userService.checkId(username);
			System.out.println("noExisteId : "+noExisteId);
			return new ResponseEntity(noExisteId,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	//*회원가입
	@PostMapping("/user_join")
	public ResponseEntity<Boolean> save(@RequestBody User user){
		System.out.println(user);
		
		try {
			return new ResponseEntity(userService.register(user),HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/user_login")
	public ResponseEntity<LoginDto> existUser(@RequestParam("username") String username, @RequestParam("password") String password) {			
		//로그인
		LoginDto loginDto =userService.checkUser(username,password);
		HttpHeaders  headers=new HttpHeaders();
		headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		headers.set("Authorization", loginDto.getToken());
		log.info("[existUser] headers : {}",headers);
		if(loginDto.getCode()!=0) {
			return new ResponseEntity(loginDto,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok().headers(headers).body(loginDto);
			
	}
	@GetMapping("/auth/token_check")
	public ResponseEntity<TokenDto> checkToken(@RequestHeader String Authorization) {
	//토큰 유효하면  username 표시
		log.info("[checkToken]새로고침 시 토큰 확인시작");
		TokenDto result= userService.expiredCheckToken(Authorization);
		
		if(result.isValidated()) {
			return new ResponseEntity<TokenDto>(result,HttpStatus.OK);
		}else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
}
