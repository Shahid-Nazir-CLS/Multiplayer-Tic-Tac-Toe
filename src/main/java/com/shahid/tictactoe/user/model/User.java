package com.shahid.tictactoe.user.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {
    @Id
    private String playerId;
    private String nickName;
    private String fullName;
    private String hashedPassword;
    private String salt;
    private Status status;
    private Date joinedOn;
    private Date lastOnline;
}
