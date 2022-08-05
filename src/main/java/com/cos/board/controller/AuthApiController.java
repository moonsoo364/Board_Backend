package com.cos.board.controller;



import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RestController;

import com.cos.board.config.XssUtil;
import com.cos.board.dto.BoardDto;

import com.cos.board.dto.TokenDto;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.model.Board;
import com.cos.board.service.BoardService;
import com.cos.board.service.UserService;

@RestController
public class AuthApiController {

	@Autowired
	private UserService userService;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private BoardService boardService;
	
	@PostMapping("api/auth/expiredCheckToken")
	public ResponseEntity<TokenDto> checkToken(@RequestHeader String Authorization) {
	//토큰 유효하면  username 표시
		TokenDto result= userService.expiredCheckToken(Authorization);
		
		if(result.isValidated()) {
			return new ResponseEntity<TokenDto>(result,HttpStatus.OK);
		}else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/api/auth/insertBoard")
	public ResponseEntity insertBoard(@RequestBody BoardDto board,@RequestHeader String Authorization ) {
		//insertBoard에서는 토큰에서 username 가지고 온다.
		System.out.println(Authorization);
		XssUtil xssUtil = new XssUtil();
		System.out.println(xssUtil.cleanXSS(board.getContent()));
		board.setContent(xssUtil.cleanXSS(board.getContent()));
		board.setTitle(xssUtil.cleanXSS(board.getTitle()));
		if(boardService.insertBoardData(board,Authorization))
		{
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//글 하나 상세보기
	@PostMapping("/api/auth/boardDetail")
	public ResponseEntity<BoardDto> selectWriting(@RequestBody Board board,@RequestHeader String Authorization){
		//
		System.out.println(board.getId());
		try {
			jwtTokenProvider.vallidateToken(Authorization);
		}catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		BoardDto result= boardService.searchBoardNum(board.getId(), Authorization);
		System.out.println(result);
		return new ResponseEntity(result,HttpStatus.OK);
	}
	@PostMapping("/api/auth/selectAll")
	public ResponseEntity<ArrayList<BoardDto>> selectAllWriting(@RequestHeader String Authorization) {
		try {
			jwtTokenProvider.vallidateToken(Authorization);
		}catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		String username =jwtTokenProvider.getUsername(Authorization);
		
		return new ResponseEntity(boardService.selectAllBoard(username),HttpStatus.OK);
	}
	@PostMapping("/api/auth/updateBoard")
	public ResponseEntity updateBoard(@RequestBody Board board, @RequestHeader String Authorization)
	{
		try {
			jwtTokenProvider.vallidateToken(Authorization);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		XssUtil xssUtil = new XssUtil();
		String content=xssUtil.cleanXSS(board.getContent());
		String title=xssUtil.cleanXSS(board.getTitle());
		board.setContent(content);
		board.setTitle(title);
		boardService.updateById(board);
		
		return new ResponseEntity(HttpStatus.OK);
	}
	@PostMapping("/api/auth/deleteBoard")
	public ResponseEntity deleteBoard(@RequestBody Board board, @RequestHeader String Authorization)
	{
		System.out.println(board);
		try {
			jwtTokenProvider.vallidateToken(Authorization);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		boardService.deleteById(board);
		
		return new ResponseEntity(HttpStatus.OK);
	}
}
