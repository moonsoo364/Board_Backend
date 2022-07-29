package com.cos.board.repository;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;

import com.cos.board.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	
	
	boolean existsByUsername(String username);
	
	User getByUsername(String username);
	Optional<User>  findByUsername(String username);
	
	
}
