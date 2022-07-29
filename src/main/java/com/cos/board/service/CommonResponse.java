package com.cos.board.service;

import org.springframework.http.HttpStatus;

public enum CommonResponse {
	SUCCESS(0,HttpStatus.OK),FAIL_PW(1,HttpStatus.INTERNAL_SERVER_ERROR),FAIL_Name(2,HttpStatus.INTERNAL_SERVER_ERROR);
	
	int code;
	HttpStatus status;
	
	CommonResponse(int code, HttpStatus status){
		this.code =code;
		this.status= status;
	}
	public int getCode() {
		return code;
	}
	public HttpStatus getStatus() {
		return status;
	}
}
