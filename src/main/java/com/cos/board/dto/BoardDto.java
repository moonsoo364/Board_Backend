package com.cos.board.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.cos.board.model.Board;

import lombok.Data;

@Data
public class BoardDto {
	private String title;
	private String content;
	private String username;
	private Timestamp createTime;
	private List<String> filename =new ArrayList<>();
	private int id;
	
	public BoardDto() {
		
	}
	
	public BoardDto(Board boardResult,String userName) {
		this.content = boardResult.getContent();
		this.id = boardResult.getId();
		this.title = boardResult.getTitle();
		this.username = boardResult.getUser().getUsername();
		this.filename=boardResult.getFilename();
	}
}
