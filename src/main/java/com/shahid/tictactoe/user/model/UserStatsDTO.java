package com.shahid.tictactoe.user.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDTO {
    private String nickname;
    private String fullName;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private int draws;
    private Date joinedOn;
    private Date lastOnline;
    private String status;
}
