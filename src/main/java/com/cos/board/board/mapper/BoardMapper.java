package com.cos.board.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.cos.board.board.model.Board;

@Mapper
public interface BoardMapper {
	@Select("SELECT * FROM BOARD WHERE ID= #{id}")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	List<Board> selectBoard(@Param("id")int id);
	
	@Update("UPDATE BOARD SET CONTENT=#{content}, TITLE=#{title}, CREATEDATE=NOW() WHERE ID =#{id}")
	@Options(useGeneratedKeys =true, keyProperty="id")
	void updateBoard(@Param("content")String content,@Param("title")String title,@Param("id") int id);
	
	@Delete("DELETE FROM board WHERE ID= #{id}")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void deleteBoard(@Param("id")int id);
	
	@Update("UPDATE BOARD SET ID =@COUNT:=@COUNT+1")
	void updateId();
}
