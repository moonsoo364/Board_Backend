package com.cos.board.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.dto.BoardDto;
import com.cos.board.model.Board;

public interface BoardRepository extends JpaRepository<Board, Integer>{

	@Query(value="insert into board(userid,title,content) values(:userid,:title,:content)",nativeQuery= true)
	@Transactional
	public void insertBoard(@Param("userid")int userid,@Param("title")String title, @Param("content")String content);

	public List<Board> findById(int id);
	
	@Query(value="select b from Board b")
	public ArrayList<Board> findAll();
}
