package com.shahid.tictactoe.game.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document
public class Game {

    @Id
    private String gameId;
    private String chatRoomId;
    private String player1Id;
    private String player2Id;
    private List<List<String>> board;
    private String currentPlayer;
    private String status;
    private String player1Symbol;
    private String player2Symbol;
    private Date startTime;
}
