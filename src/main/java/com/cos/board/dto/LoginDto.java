package com.cos.board.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDto {
	private String token;
	private int expiredTime;
	private int code;
	private boolean admin;
	private String username;
	private int id;
	public LoginDto(String token,int expiredTime,int code,boolean admin,String username,int id){
		this.token=token;
		this.expiredTime=expiredTime;
		this.code=code;
		this.admin=admin;
		this.username=username;
		this.id=id;
	}

}
