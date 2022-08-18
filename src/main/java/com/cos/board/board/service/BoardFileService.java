package com.cos.board.board.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Writer;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cos.board.board.dto.BoardDto;
import com.cos.board.board.dto.DownloadFileDto;
import com.cos.board.board.dto.SelectBoardDto;
import com.cos.board.board.dto.UpdateFileDto;
import com.cos.board.board.mapper.BoardMapper;
import com.cos.board.board.model.Board;
import com.cos.board.board.model.BoardFile;
import com.cos.board.board.repository.BoardFileRepository;
import com.cos.board.board.repository.BoardRepository;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.jwt.dto.Token;
import com.cos.board.jwt.dto.TokenDto;
import com.cos.board.user.model.User;
import com.cos.board.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BoardFileService {
	
	
	

	@Autowired
	private Environment env;
	
	
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void fileUploadInlocal(List<MultipartFile>fileList) {
		//fileupload시 게시물이 현재 생성되지 않은 상태이기 때문에
		// 게시판 글이 없을 때 Select문 에러 발생
		int boardId=1;
		try {
			try {
				String Query="SELECT ID FROM BOARD ORDER BY ID DESC LIMIT 1";
				boardId=(Integer)em.createNativeQuery(Query).getResultList().get(0)+1;
			} catch (Exception e) {
				log.info("[fileUploadInlocal] 첫 게시물 입니다. ");
			}
			
			String path=env.getProperty("springboot.servlet.multipart.dir")+boardId;
			File dir=new File(path);
			log.info("[fileUploadInlocal] 파일이 생성될 directory : {}",path);
			if(!dir.exists())
			{
				dir.mkdir();
				log.info("[fileUploadInlocal] 게시판 파일 디렉토리 생성 : {}",path);
			}else{
				File[] allFiles = dir.listFiles();
				for(File index :allFiles)
				{
					index.delete();
				}
				log.info("[fileUploadInlocal] 디렉토리의 기존 파일을 삭제했습니다.");
			}
			for (MultipartFile multipartFile :fileList) {
				FileOutputStream writer = new FileOutputStream(path+"/"+multipartFile.getOriginalFilename());
				log.info("[fileUploadInlocal] filename : {}",multipartFile.getOriginalFilename());
				writer.write(multipartFile.getBytes());
				writer.close();
				
			}
			log.info("[fileUploadInlocal] 모든 파일이 업로드 되었습니다!");

		} catch (Exception e) {
			log.info("[fileUploadInlocal] 파일 업로드 에러!");
		}
	}
	
	public DownloadFileDto fileDownloadInBrowser(BoardFile boardFile) throws FileNotFoundException {
		
		log.info("[fileDownloadInBrowser] 파일 다운로드 시작");
		String path= env.getProperty("springboot.servlet.multipart.dir")+boardFile.getBoard().getId()+"/"+boardFile.getFilename();
		log.info("[fileDownloadInBrowser] 경로: {}",path);
		File file =new File(path);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		log.info("[fileDownloadInBrowser] 파일 다운로드 완료");
		return new DownloadFileDto(resource,file.length());
	}
	
	public boolean updateBoardFile(UpdateFileDto updateFileDto) {
		log.info("[updateBoardFile] {}번 게시물 파일 수정 시작",updateFileDto.getBoardId());
		String path=env.getProperty("springboot.servlet.multipart.dir")+updateFileDto.getBoardId();
		log.info("[updateBoardFile] 파일 : {}",updateFileDto.getFileList());
		log.info("[updateBoardFile] 경로 : {}",path);
		try {
			File dir = new File(path);
			if(!dir.exists()) {
				dir.mkdir();
				log.info("기존 파일이 없기 떄문에 directory 생성");
			}else {
				log.info("Directory에 파일이 존재합니다.");
			}
			for(MultipartFile index:updateFileDto.getFileList()) {
				FileOutputStream fileOutputStream=new FileOutputStream(path+"/"+index.getOriginalFilename());
				fileOutputStream.write(index.getBytes());
				log.info("[updateBoardFile] 파일 {}",index);
				fileOutputStream.close();
			}
			log.info("[updateBoardFile] 파일 업로드 성공");
			return true;
		} catch (Exception e) {
			log.warn("[updateBoardFile] 파일 업로드 실패");

		}
		
		return false;
	}


}
