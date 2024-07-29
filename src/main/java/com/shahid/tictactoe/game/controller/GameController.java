package com.shahid.tictactoe.game.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shahid.tictactoe.game.model.Game;
import com.shahid.tictactoe.game.model.GameDTO;
import com.shahid.tictactoe.game.service.GameService;
import com.shahid.tictactoe.user.model.User;
import com.shahid.tictactoe.user.service.MatchMakingService;

@Controller
@RequestMapping("/game")
public class GameController {
    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MatchMakingService matchMakingService;


    @MessageMapping("/move")
    public void makeMove(@Payload GameDTO gameDto) {
        Game game = gameService.makeMove(gameDto.getGameId(), gameDto.getRow(), gameDto.getCol());

        // send to both client and receivar
        messagingTemplate.convertAndSendToUser(game.getPlayer1Id(), "/queue/game", game);
        messagingTemplate.convertAndSendToUser(game.getPlayer2Id(), "/queue/game", game);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Game>> getGames(@PathVariable String userId){
        return ResponseEntity.ok(gameService.findAllUserGames(userId));
    }

    @GetMapping("/cancel/{userId}")
    public String cancelMatch(@PathVariable String userId){
        User user = matchMakingService.removePlayerFromMatchQueue(userId);
        return "Cancelled matching";
    }
}
