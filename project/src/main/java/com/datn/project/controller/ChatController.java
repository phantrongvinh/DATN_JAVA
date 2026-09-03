package com.datn.project.controller;

import com.datn.project.dto.chatbox.*;
import com.datn.project.security.CustomUserDetail;
import com.datn.project.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ChatResponse chat(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal CustomUserDetail principal) {
        Integer userId = principal != null ? principal.getUserID() : null;
        return chatService.chat(userId, request.getHistory(), request.getMessage());
    }
}