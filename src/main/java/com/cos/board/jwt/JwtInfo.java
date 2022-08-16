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

import com.cos.board.jwt.dto.Token;
import com.cos.board.user.model.RoleType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInfo {
	
	private final UserDetailsService userDetailsService;
	
	@Value("${springboot.jwt.secret}")
	private String secretKey ="secretKey";
	private final int tokenValidMillisecond = 1000*60*60;
	
	//Bean 속성 초기화 이후에 @PostConstruct 주석이 달린 메서드를 한 번만 호출한다.
	@PostConstruct
	protected void init() {
		log.info("[init] JwtInfo 내 sercertKey 초기화 시작");
		secretKey =Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
		log.info("[init] JwtInfo 내 secretKey 초기화 완료");	
	}

	public String getUsername(String token) {
		log.info("[getUsername] 토큰 기반 회원 구별 정보 추출 시작");
		String username =Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
		log.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료 username: {}",username);
		return username;
		
	}

	public boolean vallidateToken(String token) {
		log.info("[validateToken] 토큰 유효 체크 시작");
		try {
			Jws<Claims> claims =Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			log.info("[validateToken] 토큰 유효 체크 완료");
			
			
			return !claims.getBody().getExpiration().before(new Date());
			
		}catch(Exception e) {
			log.info("[validateToken] 토큰 유효 체크 예외 발생");
			return false;
		}
	}
	public boolean isAdmin(String token) {
		log.info("[isAdmin] 토큰 유효 체크 시작");
		try {
			Jws<Claims>claims =Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			
			boolean isUserRole=claims.getBody().get("roles").toString().equals(RoleType.ADMIN.toString());
			boolean isTimeout = claims.getBody().getExpiration().before(new Date());
			log.info("[isAdmin] Admin권한 확인 : {}",isUserRole);
			log.info("[isAdmin] 토큰 만료 확인 : {}",isTimeout);
			boolean result=isUserRole&&!isTimeout;
			log.info("[isAdmin] result : {}",result);
			return result;
		}catch(Exception e) {
			log.info("[isAdmin] 토큰 유효 체크 예외 발생 : {}",e);
			return false;
		}
		
	}
	public long restedValiDate(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			log.info("[restedValiDate] 토큰 유효 체크 완료");
			
			long restTime=claims.getBody().getExpiration().getTime();
			log.info("[restedValiDate]토큰 유효기간 : {}",restTime-new Date().getTime());
			return  restTime-new Date().getTime();
			
		}catch(Exception e) {
			log.info("[restedValiDate] 토큰 유효 체크 예외 발생");
			return 0;
		}
	}
	
}
