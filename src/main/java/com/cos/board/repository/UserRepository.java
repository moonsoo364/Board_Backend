package com.cos.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.board.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
