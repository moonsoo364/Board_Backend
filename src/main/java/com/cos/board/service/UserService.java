package com.cos.board.service;

import java.lang.reflect.Array;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.dto.InsertBoardDto;
import com.cos.board.dto.SelectBoardDto;
import com.cos.board.dto.SignInResultDto;
import com.cos.board.dto.SignUpResultDto;
import com.cos.board.dto.StatusToken;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.model.Board;
import com.cos.board.model.Token;
import com.cos.board.model.User;
import com.cos.board.model.UserMapper;
import com.cos.board.repository.BoardRepository;
import com.cos.board.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private UserMapper userMapper;
	
	
	@PersistenceContext
	private EntityManager em;

	
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Transactional
	public boolean register(User user) {
		String username =user.getUsername();
		boolean checkUser=userRepository.existsByUsername(username);
		String rawPassword =user.getPassword();
		String encPassword=encoder().encode(rawPassword);
		user.setPassword(encPassword);
		
		if(checkUser)
		{
			//중복된 아이디가 있으면
			
			return false;
		
		}else {
			//중복된 아이디가 없으면
			userRepository.save(user);
			return true;
		}
		
	
	}
	
	public boolean checkId(String username) {
		
		System.out.println("username : "+username);
		System.out.println("getByUsername : "+userRepository.getByUsername(username));
		//JPA Query문
		Query query =em.createQuery("select u from User u where u.username = ?1");
		query.setParameter(1, username);
		boolean result=query.getResultList().isEmpty();
		//MyBatis Query문
//		int result =userMapper.findByUsername(username);
		System.out.println("찾은 user id: "+result); 
		

		
		return result;
	}
	
	public SignInResultDto checkUser(String username,String password) {
		
		System.out.println("username: "+username);
		System.out.println("password: "+password);
		SignInResultDto signInResultDto =new SignInResultDto();
		
		Query query =em.createQuery("select u.password from User u where u.username = ?1");
		query.setParameter(1, username);
		
		List<String> resultList = query.getResultList();
		
		try {
			System.out.println("resultList.get(0) : "+resultList.get(0));
		}catch(Exception e) {
			System.out.println("username 없음");
			
			signInResultDto.setToken(null);
			String loginToken=signInResultDto.getToken();
			System.out.println("token null : "+loginToken);
			setFailNameResult(signInResultDto);
			System.out.println(signInResultDto);
			return signInResultDto;
			
		}
		
		String encPassword=(String)resultList.get(0);
		boolean isPasswordMatch =encoder().matches(password, encPassword);
		System.out.println("isPasswordMatch : "+isPasswordMatch);
		if(isPasswordMatch==false) {
			System.out.println("비밀번호 틀림");
			signInResultDto.setToken(null);
			setFailPWResult(signInResultDto);
			return signInResultDto;
		}
		Token setedtoken = new Token();
		setedtoken=jwtTokenProvider.createToken(username);
		
		SignInResultDto signInResultDto1 =SignInResultDto.builder()
				.token(setedtoken.getToken_key())
				.expiredTime(setedtoken.getExpiredTime())
				.build();
		System.out.println("[checkuser] signInResultDto1.getToken() :"+signInResultDto1.getToken());
		setSuccessResult(signInResultDto1);
		return signInResultDto1;
		
	}
	
	public StatusToken expiredCheckToken(String token) {
		StatusToken statusToken =new StatusToken(false, null);
		statusToken.setRestedTime(jwtTokenProvider.restedValiDate(token));
		statusToken.setValidated(jwtTokenProvider.vallidateToken(token));
		return statusToken;
	}
	
	@Transactional
	public boolean insertBoardData(InsertBoardDto boardData) {
		System.out.println("insertBoard : "+boardData);
		
		
		try {
			List<User> result = em.createQuery("select u from User u where u.username= :username",User.class)
								.setParameter("username", boardData.getUsername()).getResultList();
			System.out.println("result.get(0).getId() : "+result.get(0).getId());
			System.out.println("title :  : "+boardData.getTitle());
			boardRepository.insertBoard(result.get(0).getId(), boardData.getTitle(), boardData.getContent());
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
		
		for(SelectBoardDto row:result) {
			System.out.println("객체 값 : "+row);
		}

		return result;
	}
	
	private void setSuccessResult(SignUpResultDto result) {
		
		result.setCode(CommonResponse.SUCCESS.getCode());
		result.setStatus(CommonResponse.SUCCESS.getStatus());

	}
	private void setFailNameResult(SignUpResultDto result) {
		
		result.setCode(CommonResponse.FAIL_Name.getCode());
		result.setStatus(CommonResponse.FAIL_Name.getStatus());

	}
	private void setFailPWResult(SignUpResultDto result) {
		
		result.setCode(CommonResponse.FAIL_PW.getCode());
		result.setStatus(CommonResponse.FAIL_PW.getStatus());

	}
	
}
