package com.datn.project.dto.chatbox;

import java.util.List;

import lombok.Data;

@Data
public class ChatRequest {
    private List<ChatMessage> history;
    private String message;
}