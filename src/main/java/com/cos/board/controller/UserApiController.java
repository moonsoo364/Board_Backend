package com.cos.board.controller;


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
	@PostMapping("/name_check")
	public ResponseEntity<Boolean> checkName(@RequestBody User user){
		
		try {
			boolean noExisteId = userService.checkId(user.getUsername());
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
	
	@PostMapping("/user_login")
	public ResponseEntity<LoginDto> existUser(@RequestBody User user) {			
		//로그인
		LoginDto loginDto =userService.checkUser(user,user.getPassword());
		HttpHeaders  headers=new HttpHeaders();
		headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		headers.set("Authorization", loginDto.getToken());
		log.info("[existUser] headers : {}",headers);
		if(loginDto.getCode()!=0) {
			return new ResponseEntity(loginDto,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok().headers(headers).body(loginDto);
			
	}
	@PostMapping("/auth/token_check")
	public ResponseEntity<TokenDto> checkToken(@RequestHeader String Authorization) {
	//토큰 유효하면  username 표시
		TokenDto result= userService.expiredCheckToken(Authorization);
		
		if(result.isValidated()) {
			return new ResponseEntity<TokenDto>(result,HttpStatus.OK);
		}else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
}
