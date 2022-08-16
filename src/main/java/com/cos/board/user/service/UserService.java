package com.cos.board.user.service;




import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.board.dto.BoardDto;
import com.cos.board.board.mapper.BoardMapper;
import com.cos.board.board.model.Board;
import com.cos.board.board.repository.BoardRepository;
import com.cos.board.jwt.JwtInfo;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.jwt.dto.Token;
import com.cos.board.jwt.dto.TokenDto;
import com.cos.board.user.dto.LoginDto;
import com.cos.board.user.dto.UserDto;
import com.cos.board.user.model.RoleType;
import com.cos.board.user.model.User;
import com.cos.board.user.repository.UserRepository;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@AllArgsConstructor
@Slf4j
public class UserService {
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BoardRepository boardRepository;
	

	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private JwtInfo jwtInfo;
	
	
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
	
	public LoginDto checkUser(String username,String password) {
		

		LoginDto loginDto =new LoginDto();
		
		Query query =em.createQuery("select u from User u where u.username = ?1");
		query.setParameter(1, username);
		
		List<User> resultList = query.getResultList();
		
		try {
			log.info("[checkUser] {}",resultList);
		}catch(Exception e) {
			log.info("[checkUser] username 없음");
			loginDto.setUsername(null);
			loginDto.setCode(2);
			return loginDto;		
		}
		String encPassword =resultList.get(0).getPassword();
		String name =resultList.get(0).getUsername();
		RoleType roles =resultList.get(0).getRoles();
		boolean isPasswordMathch= encoder().matches(password, encPassword);
		int id=resultList.get(0).getId();
		if(isPasswordMathch) {
			log.info("[checkUser] 로그인 성공");
			Token token =jwtTokenProvider.createToken(username, roles);
			return new LoginDto(token.getToken_key(),token.getExpiredTime(),0,roles==RoleType.ADMIN,name,id);
		}else {
			log.info("[checkUser] password불일치");
			loginDto.setUsername(null);
			loginDto.setCode(1);
			return loginDto;
		}
		
		
	}

	public TokenDto expiredCheckToken(String token) {
		TokenDto tokenDto =new TokenDto(false, null);
		tokenDto.setRestedTime(jwtInfo.restedValiDate(token));
		tokenDto.setValidated(jwtInfo.vallidateToken(token));
		return tokenDto;
	}
	
	
}
