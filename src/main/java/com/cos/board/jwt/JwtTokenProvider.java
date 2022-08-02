package com.cos.board.jwt;

import java.nio.charset.StandardCharsets;


import java.util.Base64;
import java.util.Date;


import javax.annotation.PostConstruct;


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

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	
	private final Logger LOGGER =LoggerFactory.getLogger(JwtTokenProvider.class);
	private final UserDetailsService userDetailsService;
	
	@Value("${springboot.jwt.secret}")
	private String secretKey ="secretKey";
	private final int tokenValidMillisecond = 1000*60*60;
	
	//Bean 속성 초기화 이후에 @PostConstruct 주석이 달린 메서드를 한 번만 호출한다.
	@PostConstruct
	protected void init() {
		LOGGER.info("[init] JwtTokenProvider 내 sercertKey 초기화 시작");
		secretKey =Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
		
		LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");	
	}
	/**
	 * 
	 * @param username
	 * @param roles
	 * @return String
	 */
	public Token createToken(String username,RoleType roles) {
		LOGGER.info("[createTokrn] 토큰 생성 시작");
		Claims claims =Jwts.claims().setSubject(username);
		claims.put("roles", roles);
		Date now =new Date();
		Token tokenInfo =new Token();
		LOGGER.info("[createTokrn] claims 설정 완료");
		String token =Jwts.builder()
				.setClaims(claims)//body: claims정보
				.setIssuedAt(now)//jwt 생성 날짜
				.setExpiration(new Date(now.getTime()+tokenValidMillisecond))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
		tokenInfo.setExpiredTime(tokenValidMillisecond);
		tokenInfo.setToken_key(token);
		LOGGER.info("[createToken] 토큰 생성 완료");
		LOGGER.info("[createToken] {}",extractAllClaims(token));
		
		return tokenInfo;
		
	}
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}
	public void getAuthentication(String token) {
		LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작 : ");
		UserDetails userDetails = userDetailsService.loadUserByUsername(token);
		LOGGER.info("[getAuthentication] userDetails : ");
		LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {} ",userDetails.getUsername());
	}
//	 public String resolveToken(HttpServletRequest request) {
//	        LOGGER.info("[resolveToken] HTTP 헤더에서 Token 값 추출");
//	        return request.getHeader("Authorization");
//	    }
	public String getUsername(String token) {
		LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
		String info =Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
		LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료,info: {}",info);
		return info;
		
	}
	/**
	 * Http Request Header에 설정된 토큰 값을 가져옴
	 * @param request Http Request Header
	 * @return String type Token
	 */

	public boolean vallidateToken(String token) {
		LOGGER.info("[validateToken] 토큰 유효 체크 시작");
		try {
			Jws<Claims> claims =Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			LOGGER.info("[validateToken] 토큰 유효 체크 완료");
			
			
			return !claims.getBody().getExpiration().before(new Date());
			
		}catch(Exception e) {
			LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");
			return false;
		}
	}
	public long restedValiDate(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			LOGGER.info("[restedValiDate] 토큰 유효 체크 완료");
			
			long restTime=claims.getBody().getExpiration().getTime();
			LOGGER.info("[restedValiDate]토큰 유효기간 : {}",restTime-new Date().getTime());
			return  restTime-new Date().getTime();
			
		}catch(Exception e) {
			LOGGER.info("[restedValiDate] 토큰 유효 체크 예외 발생");
			return 0;
		}
	}
	
}
