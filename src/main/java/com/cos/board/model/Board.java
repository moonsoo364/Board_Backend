package com.cos.board.model;

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
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Lob
	private String content;
	// BLOB : 64KB,
	// MEDIUMBLOB: 16MB,
	// LONGBLOB: 4GB
	@ElementCollection(fetch=FetchType.EAGER)
	private List<String> filename =new ArrayList<>();
	
	@Column
	private String title;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="userid")
	private User user;

	
	@Column(name="createDate", nullable=true, updatable=true,
			insertable=true, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createDate;

}
