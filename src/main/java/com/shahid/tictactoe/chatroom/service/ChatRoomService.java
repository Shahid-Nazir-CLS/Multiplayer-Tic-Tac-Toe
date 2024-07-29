package com.shahid.tictactoe.chatroom.service;

import org.springframework.stereotype.Service;

import com.shahid.tictactoe.chatroom.model.ChatRoom;
import com.shahid.tictactoe.chatroom.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    // public String getChatRoomIdForSenderAndReceiver(String senderId, String recipientId) {
    //     // Try to find an existing chat room with sender-recipient or recipient-sender combination
    //     Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
    //     if (chatRoomOpt.isEmpty()) {
    //         chatRoomOpt = chatRoomRepository.findBySenderIdAndRecipientId(recipientId, senderId);
    //     }

    //     // If found, return the chat room ID
    //     if (chatRoomOpt.isPresent()) {
    //         System.out.println("Chat room found");
    //         return chatRoomOpt.get().getChatRoomId();
    //     }

    //     // If not found, create a new chat room
    //     String chatRoomId = createChatRoom(senderId, recipientId);
    //     System.out.println("Created new chat room");
    //     return chatRoomId;
    // }

    // private String createChatRoom(String senderId, String recipientId) {
    //     // Create a unique chat room ID, e.g., senderId_recipientId
    //     String chatRoomId = senderId + "_" + recipientId;
    //     ChatRoom chatRoom = new ChatRoom(null, chatRoomId, senderId, recipientId);
    //     chatRoomRepository.save(chatRoom);
    //     return chatRoomId;
    // }

    public String getChatRoomForGame(String gameId){
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByGameId(gameId);

        // If found, return the chat room ID
        if (chatRoomOpt.isPresent()) {
            return chatRoomOpt.get().getId();
        }

        // If not found, create a new chat room
        String chatRoomId = createChatRoom(gameId);
        return chatRoomId;
    }

    private String createChatRoom(String gameId) {
        ChatRoom chatRoom = new ChatRoom(null, gameId);
        chatRoomRepository.save(chatRoom);
        return chatRoom.getId();
    }
}