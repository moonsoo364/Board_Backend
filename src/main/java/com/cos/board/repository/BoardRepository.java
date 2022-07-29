package com.cos.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.model.Board;

public interface BoardRepository extends JpaRepository<Board, Integer>{

	@Query(value="insert into board(userid,title,content) values(:userid,:title,:content)",nativeQuery= true)
	@Transactional
	public void insertBoard(@Param("userid")int userid,@Param("title")String title, @Param("content")String content);
}
