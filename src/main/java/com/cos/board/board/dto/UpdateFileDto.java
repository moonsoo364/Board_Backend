package com.cos.board.board.dto;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdateFileDto {
	private List<MultipartFile> fileList;
	private int boardId;
	
	public UpdateFileDto(List<MultipartFile>fileList,int boardId)
	{
		this.fileList=fileList;
		this.boardId=boardId;
		
	}
}
