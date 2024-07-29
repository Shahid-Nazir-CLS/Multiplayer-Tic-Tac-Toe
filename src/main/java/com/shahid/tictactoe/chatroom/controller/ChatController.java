package com.shahid.tictactoe.chatroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shahid.tictactoe.chat.model.ChatMessage;
import com.shahid.tictactoe.chat.service.ChatMessageService;
import com.shahid.tictactoe.chatroom.model.ChatMessageDTO;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        ChatMessageDTO recipientMsg = new ChatMessageDTO(savedMsg.getId(), savedMsg.getSenderId(), savedMsg.getRecipientId(), savedMsg.getContent());
        messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages", recipientMsg);
    }

    @GetMapping("/messages/{gameId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("gameId") String gameId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(gameId));
    }
}
