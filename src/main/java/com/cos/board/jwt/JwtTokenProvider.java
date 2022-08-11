package com.cos.board.jwt;

import java.nio.charset.StandardCharsets;


import java.util.Base64;
import java.util.Date;


import javax.annotation.PostConstruct;
import javax.persistence.EnumType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.cos.board.model.RoleType;
import com.cos.board.model.Token;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
	
	private final UserDetailsService userDetailsService;
	
	@Value("${springboot.jwt.secret}")
	private String secretKey ="secretKey";
	private final int tokenValidMillisecond = 1000*60*180;
	
	//Bean 속성 초기화 이후에 @PostConstruct 주석이 달린 메서드를 한 번만 호출한다.
	@PostConstruct
	protected void init() {
		log.info("[init] JwtTokenProvider 내 sercertKey 초기화 시작");
		secretKey =Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
		
		log.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");	
	}
	/**
	 * 
	 * @param username
	 * @param roles
	 * @return String
	 */
	public Token createToken(String username,RoleType roles) {
		log.info("[createTokrn] 토큰 생성 시작");
		Claims claims =Jwts.claims().setSubject(username);
		claims.put("roles", roles);
		Date now =new Date();
		Token tokenInfo =new Token();
		log.info("[createTokrn] claims 설정 완료");
		String token =Jwts.builder()
				.setClaims(claims)//body: claims정보
				.setIssuedAt(now)//jwt 생성 날짜
				.setExpiration(new Date(now.getTime()+tokenValidMillisecond))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
		tokenInfo.setExpiredTime(tokenValidMillisecond);
		tokenInfo.setToken_key(token);
		log.info("[createToken] 토큰 생성 완료");
		log.info("[createToken] {}",extractAllClaims(token));
		
		return tokenInfo;
		
	}
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}


	
}
