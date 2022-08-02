package com.cos.board.service;



import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.dto.LoginDto;
import com.cos.board.dto.TokenDto;
import com.cos.board.jwt.JwtTokenProvider;
import com.cos.board.mapper.BoardMapper;
import com.cos.board.model.RoleType;
import com.cos.board.model.Token;
import com.cos.board.model.User;

import com.cos.board.repository.BoardRepository;
import com.cos.board.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	
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
	
	public LoginDto checkUser(User user,String password) {
		

		LoginDto loginDto =new LoginDto();
		
		Query query =em.createQuery("select u from User u where u.username = ?1");
		query.setParameter(1, user.getUsername());
		
		List<User> resultList = query.getResultList();
		
		
		try {
			System.out.println(resultList.get(0));
		}catch(Exception e) {
			System.out.println("username 없음");
			loginDto.setUsername(null);
			loginDto.setCode(2);
	return loginDto;		
		}
		String encPassword =resultList.get(0).getPassword();
		String username =resultList.get(0).getUsername();
		RoleType roles =resultList.get(0).getRoles();
		boolean isPasswordMathch= encoder().matches(password, encPassword);
		
		if(isPasswordMathch) {
			Token token =jwtTokenProvider.createToken(username, roles);
			loginDto.setUsername(username);
			loginDto.setCode(0);
			loginDto.setAdmin(roles==RoleType.ADMIN);
			loginDto.setExpiredTime(token.getExpiredTime());
			loginDto.setToken(token.getToken_key());
			System.out.println("loginDto"+loginDto);
			return loginDto;
		}else {
			System.out.println("password 불일치");
			loginDto.setUsername(null);
			loginDto.setCode(1);
			return loginDto;
		}
		
		
	}

	
	public TokenDto expiredCheckToken(String token) {
		TokenDto tokenDto =new TokenDto(false, null);
		tokenDto.setRestedTime(jwtTokenProvider.restedValiDate(token));
		tokenDto.setValidated(jwtTokenProvider.vallidateToken(token));
		return tokenDto;
	}
	

	
}
