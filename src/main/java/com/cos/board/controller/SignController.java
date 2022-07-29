//package com.cos.board.controller;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cos.board.dto.SignInResultDto;
//import com.cos.board.dto.SignUpResultDto;
//import com.cos.board.model.User;
//import com.cos.board.service.SignService;
//import com.cos.board.service.SignServiceImpl;
//
//
//@RestController
//@RequestMapping("/api")
//public class SignController {
//	private final Logger LOGGER =LoggerFactory.getLogger(SignController.class);
//	private SignServiceImpl signServiceImpl;
//	
//	@Autowired
//	public SignController() {
//		this.signServiceImpl =signServiceImpl;
//	}
//	
//
//	@PostMapping(value = "/signin")
//    public SignInResultDto signIn(
//         @RequestBody User user)
//        throws RuntimeException {
//        LOGGER.info("[signIn] 로그인을 시도하고 있습니다. id : {}, pw : ****", user.getUsername());
//        SignInResultDto signInResultDto = signServiceImpl.signIn(user.getUsername(), user.getPassword());
//
//        if (signInResultDto.getCode() == 0) {
//            LOGGER.info("[signIn] 정상적으로 로그인되었습니다. id : {}, token : {}", user.getUsername(),
//                signInResultDto.getToken());
//        }
//        return signInResultDto;
//    }
//	@PostMapping(value = "/signup")
//    public SignUpResultDto signUp(
//         @RequestBody User user
//         ) {
//        LOGGER.info("[signUp] 회원가입을 수행합니다.  password : ****, name : {}, email : {}", user.getUsername(),
//        		user.getEmail());
//        SignUpResultDto signUpResultDto = signServiceImpl.signUp(user.getUsername(), user.getPassword(), user.getEmail());
//
//        LOGGER.info("[signUp] 회원가입을 완료했습니다. id : {}", user.getUsername());
//        return signUpResultDto;
//    }
//}
