package com.datn.project.dto.chatbox;

import lombok.Data;

@Data
public class ChatMessage {
    private String role; 
    private String content;
}