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
public class BoardService {
	
	
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private BoardFileRepository boardFileRepository;
	@Autowired
	private JwtInfo jwtInfo;
	@Autowired
	private BoardMapper boardMapper;
	@Autowired
	private Environment env;
	
	
	@PersistenceContext
	private EntityManager em;

	
	@Transactional
	public boolean insertBoardData(BoardDto boardDto,String token) {
		

		try {
			String userName = jwtInfo.getUsername(token);
			List<User> result = em.createQuery("select u from User u where u.username= :username",User.class)
								.setParameter("username", userName).getResultList();
			int insertId =0;
			if(em.createNativeQuery("select * from board").getResultList().isEmpty()){
				insertId=1;
			}
			else {
				insertId= (Integer)em.createNativeQuery("SELECT ID FROM BOARD ORDER BY ID DESC LIMIT 1").getResultList().get(0)+1;

			}
			
			
			log.info("[insertBoardData] 새로운 게시판 id : {}",insertId);
			em.persist(new Board(insertId,boardDto.getContent(),boardDto.getTitle(),result.get(0)));
			log.info("[insertBoardData] 마지막 게시글 조회 {}",boardRepository.findAllByOrderByIdDesc().get(0));
			
			int boardId= boardRepository.findAllByOrderByIdDesc().get(0).getId();
			String fileQuery="INSERT INTO BOARDFILE(boardId,filename) VALUES(:boardid,:filename)";

			boardDto.getFilename().forEach(index->{
				em.createNativeQuery(fileQuery).setParameter("boardid",boardId).setParameter("filename", index).executeUpdate();
				log.info("[insertBoardData] boardfile 쿼리 실행 확인");
			});
			log.info("[insertBoardData] boardfile 쿼리 실행 끝");
			
			
			
			return true;
		}
		catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
	public ArrayList<SelectBoardDto> getBoardData(){
	
		Query query =em.createQuery("SELECT b.id,b.title FROM Board b ORDER BY b.id DESC");
				
		List<Object[]> resultList =query.getResultList();
		ArrayList<SelectBoardDto> result = new ArrayList<>();
		for(Object[] row : resultList){
			
			int id =(int)row[0];
			String title=(String)row[1];
			result.add(new SelectBoardDto(id, title));

		}
		
		return result;
	}
	public BoardDto searchBoardNum(int id, String token) {
		
		String username = jwtInfo.getUsername(token);
		List<Board> repository = boardRepository.findById(id);//JPA
		List<BoardFile> filenames= boardFileRepository.findByBoardId(id);
		List<String> nameArray = new ArrayList<>();
		for(BoardFile name: filenames) {
			nameArray.add((String)name.getFilename());
		}
		log.info("[searchBoardNum] 게시물 번호로 조회한 파일들 : {}",nameArray);
		Board boardResult =repository.get(0);
		log.info("[searchBoardNum] boardResult : {}",boardResult);
		return new BoardDto(boardResult,boardResult.getUser().getUsername(),nameArray);
	}
	@Transactional
	public void updateById(BoardDto boardDto) {
		try {

				String selectFilenameSQl ="SELECT * FROM boardfile where boardid="+boardDto.getId();			

				if(!em.createNativeQuery(selectFilenameSQl).getResultList().isEmpty()) {
					
					String deleteFilenameSql="DELETE from boardfile where boardid="+boardDto.getId();
					em.createNativeQuery(deleteFilenameSql).executeUpdate();
		
				}else {
					log.info("[updateByID] 게시물에 등록된 파일이 없습니다.");
				}
			
			//게시물 수정 Query
			String updateBoardSql="UPDATE board SET content= :content, "
					+ "title= :title,"
					+ "createDate=NOW()"
					+ "WHERE id="+boardDto.getId();
	
			em.createNativeQuery(updateBoardSql)
								.setParameter("content", boardDto.getContent())
								.setParameter("title", boardDto.getTitle())
								.executeUpdate();
			
			log.info("[updateById] 게시물 수정 완료 ");

			String path=env.getProperty("springboot.servlet.multipart.dir")+boardDto.getId();
			File dir=new File(path);
			
			log.info("[updateById] 파일 경로 : {}",dir);
			boardDto.getPrefilename().forEach(index->{
				new File(env.getProperty("springboot.servlet.multipart.dir")+boardDto.getId()+"/"+index).delete();
				log.info("{} 파일 삭제완료",index);
			});
			
			boardDto.getFilename().forEach(index->{
				String insertQuery="INSERT INTO boardfile (boardid,filename) values(?1,?2)";
				em.createNativeQuery(insertQuery)
				.setParameter(1, boardDto.getId())
				.setParameter(2, index)
				.executeUpdate();
				log.info("파일이름 = {}  테이블에 INSERT완료",index);
				
			});
			
				log.info("[updateById] 파일 수정 완료");	
				
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	@Transactional
	public void deleteById(int id) {
		log.info("[deleteById] BoardId값으로 삭제시작");
		try {

//		String deleteQuery ="DELETE from BOARD where id="+id;
//		em.createNativeQuery(deleteQuery).executeUpdate(); JPA DELETE
			
		String Query="DELETE from BOARDFILE Where BoardId = "+id;
		em.createNativeQuery(Query).executeUpdate();
		boardMapper.deleteBoard(id);
		log.info("[deleteById] {}번 게시판 컬럼 삭제완료",id);
		String path =env.getProperty("springboot.servlet.multipart.dir")+id;
		
		File dir =new File(path);
		// directory안이 비어 있어야 삭제가능
		if(dir.exists()) {
			log.info("[deleteById] 경로: {} ",dir);
			File[] fileList=dir.listFiles();
			for(File index:fileList) {
				index.delete();
				log.info("[deleteById] 경로: {} ",dir);
			}
			if(dir.length()==0 && dir.isDirectory()) {
				dir.delete();
			}
		}else {
			log.info("[deleteById] 등록된 파일이 없습니다. ");
		}
		log.info("[deleteById] 게시물이 비었는 가? : {}",em.createNativeQuery("select * from board").getResultList().isEmpty());
		if(em.createNativeQuery("select * from board").getResultList().isEmpty())
		{
			em.createNativeQuery("ALTER TABLE BOARD AUTO_INCREMENT = 1").executeUpdate();
			log.info("[deleteById] 게시판 글 비었음");
		}else {
			em.createNativeQuery("ALTER TABLE BOARDFILE AUTO_INCREMENT=1").executeUpdate();
			em.createNativeQuery("SET @COUNT =0").executeUpdate();
			em.createNativeQuery("UPDATE BOARDFILE SET ID= @COUNT\\:\\=@COUNT+1").executeUpdate();

		}
		
		log.info("[deleteById] {}번 게시판 삭제 완료",id);
		
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
}
