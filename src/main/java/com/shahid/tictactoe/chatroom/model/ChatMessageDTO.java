package com.shahid.tictactoe.chatroom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ChatMessageDTO {

    private String id;
    private String senderId;
    private String recipientId;
    private String content;

}
