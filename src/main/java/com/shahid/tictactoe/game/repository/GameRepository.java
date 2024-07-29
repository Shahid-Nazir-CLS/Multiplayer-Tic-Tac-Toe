package com.shahid.tictactoe.game.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.shahid.tictactoe.game.model.Game;

public interface GameRepository extends MongoRepository<Game, String>{
    // return games sorted in descending order of start time
    @Query(value = "{ '$or': [ { 'player1Id': ?0 }, { 'player2Id': ?1 } ] }", sort = "{ 'startTime': -1 }")
    List<Game> findByPlayer1IdOrPlayer2Id(String player1Id, String player2Id);
}
