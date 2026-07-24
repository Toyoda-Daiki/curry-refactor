package com.example.controller;

import com.example.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {

        String message = body.get("message");
        String itemName = body.getOrDefault("itemName", "");
        String itemDescription = body.getOrDefault("itemDescription", "");

        log.info("チャットリクエスト: message={}", message);

        if (message == null || message.isBlank()) {
            log.warn("メッセージが空のリクエスト");
            return ResponseEntity.badRequest().body(Map.of("error", "メッセージが空です"));
        }

        ChatService.ChatResult result = chatService.chat(message, itemName, itemDescription);

        log.info("チャットレスポンス: reply={}", result.reply());

        return ResponseEntity.ok(Map.of("reply", result.reply()));
    }
}
