package com.cos.board.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.board.dto.BoardDto;
import com.cos.board.board.model.Board;
import com.cos.board.board.repository.BoardRepository;
import com.cos.board.user.controller.AdminApiController;
import com.cos.board.user.dto.UserDto;
import com.cos.board.user.model.User;
import com.cos.board.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminService {
	
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private UserRepository userRepository;
	
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	public ArrayList<UserDto> selectAllUser() {
		ArrayList<User> arrayList= new ArrayList<>();
		ArrayList<User> arr= userRepository.selectAllUser();
		ArrayList<UserDto> userList =new ArrayList<>();
		log.info("[selectAllUser] 관리자 권한 : 모든유저 정보 조회");
		try {
			for(User index:arr) {
				userList.add(new UserDto(index.getId(),index.getUsername(),index.getCreateDate(),index.getEmail(),index.getRoles().toString()));
			}
			return userList;
			
		} catch (Exception e) {

			System.err.println("SQL Error");
			return null;
		}
			
		
	}
	public boolean isCorrectPw(String password, int id) {
		try {
			log.info("[isCorrectPw] 중복된 비밀번호인지 확인 중..");
			String encPassword= userRepository.findById(id).get().getPassword();
			log.info("[isCorrectPw] 비밀번호 일치 유무 : {}",encoder().matches(password, encPassword));
			return encoder().matches(password, encPassword);
		} catch (Exception e) {
			return false;
		}
	}
	
	@Transactional
	public void updateUser(int id, String email, String password) {
		String encPassword =encoder().encode(password);
		try {
			userRepository.UpdateById(email, encPassword,id);
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	public List<BoardDto> selectUserWriting (int userId){
		try {
			
			List<Board>writingList= boardRepository.findByUserId(userId);
			Stream<Board> stream =writingList.stream();
			List<BoardDto> result = new ArrayList<>();
			stream.forEach(board->{
				result.add(new BoardDto(board));
				});
			return result;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		
		
		
	}
}
