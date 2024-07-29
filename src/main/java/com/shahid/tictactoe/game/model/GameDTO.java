package com.shahid.tictactoe.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private String gameId;
    private int row;
    private int col;
    private String userId;
}
