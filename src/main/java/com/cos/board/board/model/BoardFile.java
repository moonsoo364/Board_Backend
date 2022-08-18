package com.cos.board.board.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import lombok.Data;

@Entity
@Data
@SequenceGenerator(
		name="BOARDFILE_SEQ_GENERATOR",
		sequenceName="BOARDFILE_SEQ",
		initialValue=1,
		allocationSize = 1
		)
public class BoardFile {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARDFILE_SEQ_GENERATOR")
	private int id;
	

	@ManyToOne(fetch =  FetchType.EAGER)
	@JoinColumn(name="boardId")
	private Board board;
	
	@Column(nullable = false)
	private String filename;
	
}
