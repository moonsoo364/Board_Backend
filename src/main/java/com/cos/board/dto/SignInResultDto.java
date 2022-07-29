package com.cos.board.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SignInResultDto extends SignUpResultDto {

    private String token;
    private HttpStatus status;
    private int code;
    private long expiredTime;

   

}