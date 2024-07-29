package com.shahid.tictactoe.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shahid.tictactoe.chat.model.ChatMessage;
import com.shahid.tictactoe.chat.repository.ChatMessageRepository;
import com.shahid.tictactoe.chatroom.service.ChatRoomService;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        String chatId = chatRoomService.getChatRoomForGame(chatMessage.getGameId());
        chatMessage.setChatRoomId(chatId);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String gameId) {
        String chatRoomId = chatRoomService.getChatRoomForGame(gameId);
        if (chatRoomId != null) {
            // If found, return messages for this chat room
            return chatMessageRepository.findByChatRoomId(chatRoomId);
        }

        // If no chat room found in either case, return an empty list
        return new ArrayList<>();
    }
}
