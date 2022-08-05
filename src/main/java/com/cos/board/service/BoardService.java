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
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.mapper.BoardMapper;
import com.cos.board.model.Board;
import com.cos.board.model.Token;
import com.cos.board.model.User;

import com.cos.board.repository.BoardRepository;
import com.cos.board.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BoardService {
	
	
	

	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private BoardMapper boardMapper;
	
	
	@PersistenceContext
	private EntityManager em;

	
	@Transactional
	public boolean insertBoardData(BoardDto board,String token) {
		

		try {
			String userName = jwtTokenProvider.getUsername(token);
			List<User> result = em.createQuery("select u from User u where u.username= :username",User.class)
								.setParameter("username", userName).getResultList();
			System.out.println("result.get(0).getId() : "+result.get(0).getId());
//			System.out.println("title :  : "+boardData.getTitle());
			int userId=result.get(0).getId();
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
		
		String username = jwtTokenProvider.getUsername(token);
//		List<Board> result =boardMapper.selectBoard(id);//MyBatis
		List<Board> repository = boardRepository.findById(id);//JPA
		//객체지향작성법		
		Board boardResult =repository.get(0);

		return new BoardDto(boardResult,username);
	}
	@Transactional
	public void updateById(Board board) {
		System.out.println("[updateById] : "+board);
		try {
		boardMapper.updateBoard(board.getContent(), board.getTitle(), board.getId());
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	@Transactional
	public void deleteById(Board board) {
		System.out.println("[deleteById] : "+board);
		try {
		boardMapper.deleteBoard(board.getId());
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	public ArrayList<BoardDto> selectAllBoard(String username){
		ArrayList<Board> arrayList =boardRepository.findAllBoard();

		ArrayList<BoardDto> list =new ArrayList<>();
		
		
		for(Board index : arrayList) {
			
			BoardDto boardDto =new BoardDto();
			boardDto.setContent(index.getContent());
			boardDto.setUsername(index.getUser().getUsername());
			boardDto.setId(index.getId());
			boardDto.setTitle(index.getTitle());
			boardDto.setCorrectUser(username.equals(index.getUser().getUsername()));
			list.add(boardDto);
			System.out.println(boardDto);
			
			
		}
		System.out.println(list);
//		System.out.println(boardDtos);
		return list;
	}


}
