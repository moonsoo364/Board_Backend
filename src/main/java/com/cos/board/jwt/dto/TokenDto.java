package com.cos.board.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
	private boolean validated;
	private Long restedTime;

}
