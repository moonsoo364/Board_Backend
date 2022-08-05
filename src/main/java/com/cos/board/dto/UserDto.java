package com.cos.board.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserDto {

	private int id;
	private String username;
	private Timestamp createDate;
	private String email;
	private String roles;
	
}
