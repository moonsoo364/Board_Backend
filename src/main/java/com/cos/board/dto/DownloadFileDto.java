package com.cos.board.dto;

import org.springframework.core.io.InputStreamResource;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadFileDto {
	
	private InputStreamResource file;
	private Long fileSize;
	
}
