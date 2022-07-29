package com.cos.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertBoardDto {

	private String username;
	private String title;
	private String content;
}
