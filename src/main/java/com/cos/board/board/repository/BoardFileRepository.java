package com.cos.board.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.board.board.model.BoardFile;

public interface BoardFileRepository extends JpaRepository<BoardFile, Integer>{
	
 public List<BoardFile> findByBoardId(int boardId); 
}
