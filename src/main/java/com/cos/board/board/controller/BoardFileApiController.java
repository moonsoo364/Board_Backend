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
import com.cos.board.board.service.BoardFileService;
import com.cos.board.board.service.BoardService;
import com.cos.board.config.XssUtil;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.dto.TokenDto;
import com.cos.board.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class BoardFileApiController {

	@Autowired
	private UserService userService;
	@Autowired
	private JwtInfo jwtInfo;
	@Autowired
	private BoardFileService boardFileService;
	@Autowired
	private Environment env;
	
	@PostMapping("/auth/file_submit")
	public ResponseEntity<Boolean> getFiles (@RequestParam("fileList") List<MultipartFile> fileList, @RequestHeader String Authorization)
	{
		
			log.info("[getFiles] 파일 받음 {}",fileList);
			if(jwtInfo.vallidateToken(Authorization)) {
				boardFileService.fileUploadInlocal(fileList);
				return new ResponseEntity(true,HttpStatus.OK);
				
			}
			else {
			return new ResponseEntity(false,HttpStatus.UNAUTHORIZED);
			}
		
	}
	@PostMapping("/auth/file_update")
	public ResponseEntity<Boolean> updateFiles (@RequestParam List<MultipartFile> fileList,@RequestHeader int boardId, @RequestHeader String Authorization)
	{
		
			log.info("[updateFiles] 파일 받음 {}",fileList);
			log.info("[updateFiles] 게시판 번호 {}",boardId);
			if(jwtInfo.vallidateToken(Authorization)) {
				if(boardFileService.updateBoardFile(new UpdateFileDto(fileList, boardId)))
				{
					return new ResponseEntity(true,HttpStatus.OK);
				
				}
				else {
					return new ResponseEntity(false,HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
			}
			else {
				return new ResponseEntity(false,HttpStatus.UNAUTHORIZED);
			}
		
	}
	@PostMapping("/auth/download")
	public ResponseEntity<InputStreamResource> pushFile (@RequestBody BoardFile boardFile, @RequestHeader String Authorization) throws FileNotFoundException{
		//file 용량 제한 10MB
		log.info("[pushFile] 파일 : {} ",boardFile);
		
		if(jwtInfo.vallidateToken(Authorization)) {
			
			HttpHeaders headers =new HttpHeaders();
			//브라우저에서 해당 파일을 캐싱하지 않기 위한 header설정
			headers.add("Cache-Control","no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			DownloadFileDto resource =boardFileService.fileDownloadInBrowser(boardFile);
			
			return ResponseEntity.ok()
					.headers(headers)
					.contentLength(resource.getFileSize())
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(resource.getFile());
			
		}
		log.info("[pushFile] 유효하지 않은 토큰");
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
}
