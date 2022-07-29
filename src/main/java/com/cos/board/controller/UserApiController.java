package com.cos.board.controller;


import java.util.ArrayList;
import java.util.HashMap;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.board.config.XssUtil;
import com.cos.board.dto.InsertBoardDto;
import com.cos.board.dto.SelectBoardDto;
import com.cos.board.dto.SignInResultDto;
import com.cos.board.dto.StatusToken;
import com.cos.board.dto.WriteNumDto;
import com.cos.board.model.Board;
import com.cos.board.model.User;
import com.cos.board.repository.BoardRepository;
import com.cos.board.service.UserService;


@RestController
@RequestMapping("/api")
public class UserApiController {
	@Autowired
	private UserService userService;
	@Autowired
	private BoardRepository boardRepository;

	public UserApiController(UserService userService) {
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
		String username =user.getUsername();
		 
		boolean joinUser=userService.register(user);
		System.out.println("joinUser : "+joinUser);
		
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
	public ResponseEntity<SignInResultDto> existUser(@RequestBody User user) {			
		SignInResultDto signInResultDto =userService.checkUser(user.getUsername(), user.getPassword());
			
		String loginToken = signInResultDto.getToken();
		HttpStatus status =signInResultDto.getStatus();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("AUTHORIZATION_HEADER",loginToken);
		
		return new ResponseEntity<SignInResultDto>(signInResultDto,httpHeaders,status);
			
	}
	

	
	@PostMapping("auth/expiredCheckToken")
	public ResponseEntity<StatusToken> checkToken(@RequestBody HashMap<String, String> token) {
	

		
		String outputToken=token.get("token");
		System.out.println("token :"+outputToken);
		StatusToken result= userService.expiredCheckToken(outputToken);
		System.out.println("result.isValidated() : "+result.isValidated());
		if(result.isValidated()) {
			return new ResponseEntity<StatusToken>(result,HttpStatus.OK);
		}else {
			return new ResponseEntity<StatusToken>(result,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("auth/insertBoard")
	public ResponseEntity insertBoard(@RequestBody InsertBoardDto boardData ) {
		//<> script
		System.out.println(boardData);
		XssUtil xssUtil = new XssUtil();
		System.out.println(xssUtil.cleanXSS(boardData.getContent()));
		boardData.setContent(xssUtil.cleanXSS(boardData.getContent()));
		boardData.setTitle(xssUtil.cleanXSS(boardData.getTitle()));
		if(userService.insertBoardData(boardData))
		{
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("auth/getBoard")
	public ResponseEntity<ArrayList<SelectBoardDto>> getBoard() {
		try {
		ArrayList<SelectBoardDto> boardList= userService.getBoardData();
			return new ResponseEntity(boardList,HttpStatus.OK);
		}catch (Exception e){
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	@PostMapping("auth/selectWriting")
	public ResponseEntity<Board> selectWriting(@RequestBody WriteNumDto writeNumDto){
		System.out.println(writeNumDto.getNum());
		
		return new ResponseEntity(HttpStatus.OK);
	}
}
