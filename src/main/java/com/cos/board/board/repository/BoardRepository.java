package com.cos.board.board.repository;

import java.util.ArrayList;



import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;



import com.cos.board.board.model.Board;

public interface BoardRepository extends JpaRepository<Board, Integer>{

	@Query(value="insert into board(userid,title,content) values(:userid,:title,:content)",nativeQuery= true)
	@Modifying(clearAutomatically = true)
	public void insertBoard(@Param("userid")int userid,@Param("title")String title, @Param("content")String content);
	
//	@Query(value="INSERT INTO Board (userid,title,content) values(:userid,:title,:content)")
//	@Modifying(clearAutomatically = true)
//	public void insertData(@Param("userid")int userid,@Param("title")String title, @Param("content")String content);
	
	
	public List<Board> findById(int id);
	
	@Query(value="select b from Board b")
	public ArrayList<Board> findAllBoard();
	
	public List<Board> findByUserId(int userId);
	public List<Board> findAllByOrderByIdDesc();
	
}
