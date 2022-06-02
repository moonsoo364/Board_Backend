package com.cos.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor//모든 필드를 파라메터 값으로 갖는 생성자 생성
public class ResponseDto<T> {
	
	int status;
	T data;

}
