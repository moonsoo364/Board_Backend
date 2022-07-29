package com.cos.board.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 예제 13.29
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class SignUpResultDto {

   

    private int code;

    private HttpStatus status;

	

}
