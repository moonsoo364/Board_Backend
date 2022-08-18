package com.cos.board.board.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.cos.board.board.model.Board;

import lombok.Data;

@Data
public class BoardDto {
	private String title;
	private String content;
	private String username;
	private Timestamp createTime;
	private int userId;
	private List<String> filename =new ArrayList<>();
	private List<String> prefilename =new ArrayList<>();
	private int id;
	
	public BoardDto() {}
	
	public BoardDto(Board board) {
		this.title = board.getTitle();
		this.content = board.getContent();
		this.username = board.getUser().getUsername();
		this.createTime = board.getCreateDate();
		this.id = board.getId();
		
	}
	
	public BoardDto(Board boardResult,String userName,List<String> filename) {
		this.content = boardResult.getContent();
		this.id = boardResult.getId();
		this.title = boardResult.getTitle();
		this.username = boardResult.getUser().getUsername();
		this.filename=filename;
		
	}
}
