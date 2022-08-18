package com.cos.board.board.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.cos.board.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class Board {
	
	// BLOB : 64KB,
	// MEDIUMBLOB: 16MB,
	// LONGBLOB: 4GB
	
	
	@Id
	private int id;
	
	@Lob
	private String content;
	
	@Column
	private String title;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="userid")
	private User user;

	
	@Column(name="createDate", nullable=true, updatable=true,
			insertable=true, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createDate;

	public Board(int id,String content, String title, User user){
		this.id =id;
		this.content=content;
		this.title=title;
		this.user=user;
	}
}
