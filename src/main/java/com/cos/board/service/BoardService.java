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
	public boolean insertBoardData(BoardDto board,String token) {
		

		try {
			String userName = jwtInfo.getUsername(token);
			List<User> result = em.createQuery("select u from User u where u.username= :username",User.class)
								.setParameter("username", userName).getResultList();
			
			int userId=result.get(0).getId();
			log.info("[insertBoardData] 유저 id : {}",userId);
			boardRepository.insertBoard(userId, board.getTitle(), board.getContent());
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
		//객체지향작성법		
		Board boardResult =repository.get(0);

		return new BoardDto(boardResult,username);
	}
	@Transactional
	public void updateById(Board board) {
		try {
		boardMapper.updateBoard(board.getContent(), board.getTitle(), board.getId());
		log.info("[updateById] 게시물 수정 완료");
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
