package com.cos.board.service;


import java.util.ArrayList;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.dto.BoardDto;
import com.cos.board.dto.SelectBoardDto;
import com.cos.board.dto.TokenDto;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.mapper.BoardMapper;
import com.cos.board.model.Board;
import com.cos.board.model.Token;
import com.cos.board.model.User;

import com.cos.board.repository.BoardRepository;
import com.cos.board.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BoardService {
	
	
	

	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private JwtInfo jwtInfo;
	@Autowired
	private BoardMapper boardMapper;
	
	
	@PersistenceContext
	private EntityManager em;

	
	@Transactional
	public boolean insertBoardData(Board board,String token) {
		

		try {
			String userName = jwtInfo.getUsername(token);
			List<User> result = em.createQuery("select u from User u where u.username= :username",User.class)
								.setParameter("username", userName).getResultList();
			
			User userId=result.get(0);
			
			log.info("[insertBoardData] 유저 id : {}",userId);
			board.setUser(userId);
			log.info("[insertBoardData] board : {}",board);
			em.persist(board);
//			boardRepository.insertBoard(userId, board.getTitle(), board.getContent());
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
//		List<Board> result =boardMapper.selectBoard(id);//MyBatis
		List<Board> repository = boardRepository.findById(id);//JPA
	
		Board boardResult =repository.get(0);
		log.info("[searchBoardNum] boardResult : {}",boardResult);
		return new BoardDto(boardResult,username);
	}
	@Transactional
	public void updateById(Board board) {
		try {
			
				try {
				String selectFilenameSQl ="SELECT * FROM board_filename where Board_id="+board.getId();
				
				
				if(!em.createNativeQuery(selectFilenameSQl).getResultList().isEmpty()) {
					
					String deleteFilenameSql="DELETE from board_filename where Board_id="+board.getId();
					em.createNativeQuery(deleteFilenameSql).executeUpdate();

				}else {
					log.info("[updateByID] 게시물에 등록된 파일이 없습니다.");
				}
				}catch(Exception e) {
					log.info("[updateById] sql에러");
				}
				//게시물 수정 Query
				String updateBoardSql="UPDATE board SET content= :content, "
						+ "title= :title,"
						+ "createDate=NOW()"
						+ "WHERE id="+board.getId();

				int excuteCount=em.createNativeQuery(updateBoardSql)
									.setParameter("content", board.getContent())
									.setParameter("title", board.getTitle())
									.executeUpdate();
				
				log.info("[updateById] 게시물 수정 완료 Update 실행 횟수 : {}",excuteCount);
				//board_filename 테이블 insert
				board.getFilename().forEach(index->{
					String insertQuery="INSERT INTO board_filename (Board_id,filename) values(?1,?2)";
					em.createNativeQuery(insertQuery)
					.setParameter(1, board.getId())
					.setParameter(2, index)
					.executeUpdate();
					
				});
				
				
				
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	@Transactional
	public void deleteById(Board board) {
		log.info("[deleteById] BoardId값으로 삭제중...");
		try {
		boardMapper.deleteBoard(board.getId());
		}catch(Exception e) {
			System.out.println(e);
		}
	}



}
