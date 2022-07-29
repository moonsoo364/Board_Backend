package com.cos.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusToken {
	private boolean validated;
	private Long restedTime;

}
