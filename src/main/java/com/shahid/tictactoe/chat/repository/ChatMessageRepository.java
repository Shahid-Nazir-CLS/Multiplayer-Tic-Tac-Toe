package com.shahid.tictactoe.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shahid.tictactoe.chat.model.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatRoomId(String chatId);
}