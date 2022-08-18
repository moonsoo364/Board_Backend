package com.cos.board.user.repository;

import java.util.ArrayList;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cos.board.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	
	
	boolean existsByUsername(String username);
	User getByUsername(String username);
	Optional<User> findByUsername(String username);
	
	@Query(value="SELECT u FROM User u")
	ArrayList<User> selectAllUser();
	@Modifying(clearAutomatically = true)
	@Query(value="UPDATE User u SET u.email = :email, u.password = :password WHERE u.id = :id ")
	void UpdateById(String email,String password,int id);
	
	
	
	
	
	
	
}
