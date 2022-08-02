package com.cos.board.dto;

import lombok.Data;

@Data
public class BoardDto {
	private boolean correctUser;
	private String title;
	private String content;
	private String username;
	private int id;
}
