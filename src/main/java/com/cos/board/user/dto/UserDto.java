package com.cos.board.user.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserDto {

	private int id;
	private String username;
	private Timestamp createDate;
	private String email;
	private String roles;
	public UserDto(int id,String username,Timestamp createDate,String email,String roles){
		
		this.id =id;
		this.username =username;
		this.createDate =createDate;
		this.email=email;
		this.roles=roles;
	}
}
