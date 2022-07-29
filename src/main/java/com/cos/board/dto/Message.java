package com.cos.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private StatusEnum status;
    private String message;
    private Object data;

}