package com.cos.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.board.model.RoleType;
import com.cos.board.model.User;
import com.cos.board.repository.UserRepository;

@Service
public class UserService {
	
	
	
	@Autowired
	private UserRepository userRepository;
	
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Transactional
	public int register(User user) {
		user.setRole(RoleType.USER);
		String rawPassword =user.getPassword();
		String encPassword=encoder().encode(rawPassword);
		user.setPassword(encPassword);
		try {
			userRepository.save(user);
			return 1;
		}catch(Exception e) {
			return -1;
		}
	}
}
