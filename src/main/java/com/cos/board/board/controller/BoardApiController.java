package com.cos.board.board.controller;



import java.io.BufferedInputStream;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cos.board.board.dto.BoardDto;
import com.cos.board.board.dto.DownloadFileDto;
import com.cos.board.board.dto.SelectBoardDto;
import com.cos.board.board.dto.UpdateFileDto;
import com.cos.board.board.model.Board;
import com.cos.board.board.model.BoardFile;
import com.cos.board.board.service.BoardService;
import com.cos.board.config.XssUtil;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.dto.TokenDto;
import com.cos.board.user.service.UserService;

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
	@Autowired
	private Environment env;
	
	@GetMapping("/board_select")
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
	public ResponseEntity insertBoard(@RequestBody BoardDto boardDto, @RequestHeader String Authorization ) {
		//insertBoard에서는 토큰에서 username 가지고 온다.
		try {
			log.info("[insertBoard] 받은 게시물 데이터 : {}",boardDto);
		} catch (Exception e) {
			log.info("[insertBaord] 게시물 받기 실패");
		}
		
		log.info("[insertBoard] 받은 유저 토큰 : {}",Authorization);
		XssUtil xssUtil = new XssUtil();
		boardDto.setContent(xssUtil.cleanXSS(boardDto.getContent()));
		boardDto.setTitle(xssUtil.cleanXSS(boardDto.getTitle()));
		if(boardService.insertBoardData(boardDto,Authorization))
		{
			return new ResponseEntity(HttpStatus.OK);
		}
		else {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//글 하나 상세보기
	@GetMapping("/auth/board_detail")
	public ResponseEntity<BoardDto> selectWriting(@RequestHeader String Authorization, @RequestParam("id") int id){
		log.info("[selectWriting] 게시글 상세보기 조회 시작 게시글 ID:{}",id);
		try {
			jwtInfo.vallidateToken(Authorization);
		}catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		BoardDto result= boardService.searchBoardNum(id, Authorization);
		log.info("[selectWriting] body에 전달하는 Data : {}",result);
		return new ResponseEntity(result,HttpStatus.OK);
	}
	@PutMapping("/auth/board_update")
	public ResponseEntity updateBoard(@RequestBody BoardDto boardDto, @RequestHeader String Authorization)
	{
		try {
			jwtInfo.vallidateToken(Authorization);
			
			XssUtil xssUtil = new XssUtil();
			String content=xssUtil.cleanXSS(boardDto.getContent());
			String title=xssUtil.cleanXSS(boardDto.getTitle());
			
			
			boardDto.setContent(content);
			boardDto.setTitle(title);
			
			log.info("[updateBoard] 게시물 수정 시작 : {}",boardDto);
			boardService.updateById(boardDto);
			
			return new ResponseEntity(HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	@DeleteMapping("/auth/board_delete")
	public ResponseEntity deleteBoard(@RequestParam("id") int id, @RequestHeader String Authorization)
	{
		log.info("[deleteBoard] 삭제 요청 시작 : {}",id);
		try {
			jwtInfo.vallidateToken(Authorization);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		boardService.deleteById(id);
		
		return new ResponseEntity(HttpStatus.OK);
	}
}
