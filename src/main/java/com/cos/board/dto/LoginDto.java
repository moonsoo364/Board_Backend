package com.cos.board.dto;

import lombok.Data;

@Data
public class LoginDto {
	private String token;
	private int expiredTime;
	private int code;
	private boolean admin;
	private String username;

}
