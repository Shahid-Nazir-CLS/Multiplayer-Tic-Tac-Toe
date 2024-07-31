package com.shahid.tictactoe.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shahid.tictactoe.game.model.Game;
import com.shahid.tictactoe.game.model.GameDTO;
import com.shahid.tictactoe.game.service.GameService;
import com.shahid.tictactoe.user.model.User;
import com.shahid.tictactoe.user.model.UserStatsDTO;
import com.shahid.tictactoe.user.service.MatchMakingService;
import com.shahid.tictactoe.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private final MatchMakingService matchMakingService;

    @Autowired
    private GameService gameService;

    /**
     * Handles adding a new user through WebSocket.
     * This method is triggered when a message is sent to /user/addUser.
     * The response is broadcasted to the public topic.
     *
     * @param user the user to be added
     * @return the added user
     */
    @MessageMapping("user.addUser")
    @SendTo("/topic/public")
    public User addUser(@Payload User user) { 
        return user;
    }

    /**
     * Handles user login via HTTP POST.
     * Authenticates or registers the user and returns the user details.
     *
     * @param user the user details for login
     * @return the authenticated or registered user
     */
    @PostMapping("/user/login")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.authenticateOrRegisterUser(user));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Handles user disconnection through WebSocket.
     * This method is triggered when a message is sent to /user/disconnectUser.
     * The response is broadcasted to the public topic.
     *
     * @param user the user to be disconnected
     * @return the disconnected user
     */
    @MessageMapping("user.disconnectUser")
    @SendTo("/topic/public")
    public User disconnectUser(@Payload User user) {
        System.out.println("User logged out");
        userService.disconnect(user);
        return user;
    }

    /**
     * Handles user matchmaking through WebSocket.
     * This method is triggered when a message is sent to /user/matchUser.
     * If a match is found, it notifies both users and creates a new game.
     *
     * @param user the user requesting a match
     * @return the user
     */
    @MessageMapping("user.matchUser")
    public User matchUser(@Payload User user) {
        User opponent = matchMakingService.matchPlayer(user);

        if (opponent != null) {
            Game game = gameService.createNewGame(user.getNickName(), opponent.getNickName());
            messagingTemplate.convertAndSendToUser(user.getNickName(), "/queue/gamestart", game);
            messagingTemplate.convertAndSendToUser(opponent.getNickName(), "/queue/gamestart", game);
        }
        return user;
    }

    /**
     * Handles user leaving the game through WebSocket.
     * This method is triggered when a message is sent to /user/leaveGame.
     * It updates the game state and notifies the opponent if applicable.
     *
     * @param gameDto the game details
     */
    @MessageMapping("user.leaveGame")
    public void leaveGame(@Payload GameDTO gameDto) {
        System.out.println("User cancelled game: " + gameDto);
        
        Game game = gameService.updateGameOnPlayerLeaving(gameDto.getGameId(), gameDto.getUserId());

        if (game != null) {
            String winningPlayer = gameDto.getUserId().equals(game.getPlayer1Id()) ? game.getPlayer2Id() : game.getPlayer1Id();
            System.out.println("After winning: " + game);
            messagingTemplate.convertAndSendToUser(winningPlayer, "/queue/game", game);
        }
    }

    /**
     * Retrieves a list of connected users.
     *
     * @return a list of connected users
     */
    @GetMapping("/user/connected")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    /**
     * Retrieves statistics for a specific user.
     *
     * @param userId the ID of the user
     * @return the user's statistics
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<UserStatsDTO> getPlayerStats(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getPlayerStats(userId));
    }

    /**
     * Retrieves the leaderboard of top users.
     *
     * @return a list of top users based on statistics
     */
    @GetMapping("/user/leaderboard")
    public ResponseEntity<List<UserStatsDTO>> getLeaderBoard() {
        return ResponseEntity.ok(userService.findAllTopUsers());
    }
}