package com.cos.board.controller;



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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	@Autowired
	private Environment env;
	
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
	public ResponseEntity insertBoard(@RequestBody Board board,@RequestHeader String Authorization ) {
		//insertBoard에서는 토큰에서 username 가지고 온다.
		try {
			log.info("[insertBoard] 받은 게시물 데이터 : {}",board);
		} catch (Exception e) {
			log.info("[insertBaord] 게시물 받기 실패");
		}
		
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
	@PostMapping("/auth/file_submit")
	public ResponseEntity<Boolean> getFiles (@RequestParam("fileList") List<MultipartFile> fileList, @RequestHeader String Authorization)
	{
		log.info("[getFiles] 파일 받음");
			if(jwtInfo.vallidateToken(Authorization)) {
				
				try {
					for (MultipartFile multipartFile :fileList) {
						FileOutputStream writer = new FileOutputStream(env.getProperty("springboot.servlet.multipart.dir")+multipartFile.getOriginalFilename());
						log.info("[getFiles] filename : {}",multipartFile.getOriginalFilename());
						writer.write(multipartFile.getBytes());
						writer.close();
						return new ResponseEntity(true,HttpStatus.OK);
					}
				} catch (Exception e) {
					log.info("[getFiles] 파일 용량 초과!");
					return new ResponseEntity(false,HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
			
				
			}

			return new ResponseEntity(false,HttpStatus.UNAUTHORIZED);
			
		
	}
	@PostMapping("/auth/download")
	public ResponseEntity<InputStreamResource> pushFile (@RequestBody String filename, @RequestHeader String Authorization) throws FileNotFoundException{
		//file 용량 제한 10MB
		log.info("[pushFile] 파일 이름: {} , token: {}",filename,Authorization);
		
		if(jwtInfo.vallidateToken(Authorization)) {
			String path= env.getProperty("springboot.servlet.multipart.dir")+filename;

			File file =new File(path);
			log.info("[pushFIle] dir : {} ",file.getName());
			HttpHeaders headers =new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename);
			headers.add("Cache-Control","no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			log.info("[pushFile] 파일 다운로드 시작");
			return ResponseEntity.ok()
					.headers(headers)
					.contentLength(file.length())
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(resource);
			
		
		}
		log.info("[pushFile] 유효하지 않은 토큰");
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
}
