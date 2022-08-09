package com.cos.board.controller;



import java.util.ArrayList;

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
import com.cos.board.dto.SelectBoardDto;
import com.cos.board.dto.TokenDto;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.model.Board;
import com.cos.board.service.BoardService;
import com.cos.board.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class BoardApiController {

	@Autowired
	private UserService userService;
	@Autowired
	private JwtInfo jwtInfo;
	@Autowired
	private BoardService boardService;
	
	@PostMapping("/board_select")
	public ResponseEntity<ArrayList<SelectBoardDto>> getBoard() {
		//게시글 목록
		try {
		ArrayList<SelectBoardDto> boardList= boardService.getBoardData();
			return new ResponseEntity(boardList,HttpStatus.OK);
		}catch (Exception e){
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}

	@PostMapping("/auth/board_insert")
	public ResponseEntity insertBoard(@RequestBody BoardDto board,@RequestHeader String Authorization ) {
		//insertBoard에서는 토큰에서 username 가지고 온다.
		log.info("[insertBoard] 받은 게시물 데이터 : {}",board);
		log.info("[insertBoard] 받은 유저 토큰 : {}",Authorization);
		XssUtil xssUtil = new XssUtil();
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
	@PostMapping("/auth/board_detail")
	public ResponseEntity<BoardDto> selectWriting(@RequestBody Board board,@RequestHeader String Authorization){
		log.info("[selectWriting] 게시글 상세보기 조회 시작 게시글 ID:{}",board.getId());
		try {
			jwtInfo.vallidateToken(Authorization);
		}catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		BoardDto result= boardService.searchBoardNum(board.getId(), Authorization);
		log.info("[selectWriting] body에 전달하는 Data : {}",result);
		return new ResponseEntity(result,HttpStatus.OK);
	}
	@PostMapping("/auth/board_update")
	public ResponseEntity updateBoard(@RequestBody Board board, @RequestHeader String Authorization)
	{
		try {
			jwtInfo.vallidateToken(Authorization);
			XssUtil xssUtil = new XssUtil();
			String content=xssUtil.cleanXSS(board.getContent());
			String title=xssUtil.cleanXSS(board.getTitle());
			board.setContent(content);
			board.setTitle(title);
			log.info("[updateBoard] 게시물 수정 시작 : {}",board);
			boardService.updateById(board);
			
			return new ResponseEntity(HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	@PostMapping("/auth/board_delete")
	public ResponseEntity deleteBoard(@RequestBody Board board, @RequestHeader String Authorization)
	{
		log.info("[deleteBoard] 삭제 요청 시작 : {}",board);
		try {
			jwtInfo.vallidateToken(Authorization);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		boardService.deleteById(board);
		
		return new ResponseEntity(HttpStatus.OK);
	}
}
