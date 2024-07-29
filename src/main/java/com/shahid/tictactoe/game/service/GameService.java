package com.shahid.tictactoe.game.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shahid.tictactoe.chatroom.model.ChatRoom;
import com.shahid.tictactoe.chatroom.repository.ChatRoomRepository;
import com.shahid.tictactoe.game.model.Game;
import com.shahid.tictactoe.game.repository.GameRepository;
import com.shahid.tictactoe.user.model.User;
import com.shahid.tictactoe.user.model.UserStatsDTO;
import com.shahid.tictactoe.user.repository.UserRepository;



@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public List<Game> findAllUserGames(String userId){
        return gameRepository.findByPlayer1IdOrPlayer2Id(userId, userId);
    }

    public Game findGameById(String gameId){
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if(gameOpt.isPresent()){
            return gameOpt.get();
        }
        return null;
    }

    public Game createNewGame(String senderId, String receiverId){
        List<List<String>> board = new ArrayList<>();
        String status = "IN_PROGRESS";

        // create new board
        for (int i = 0; i < 3; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                row.add("-");
            }
            board.add(row);
        }

        // choose randomly receivar or sender as first turn 
        String currentPlayer;
        String player1Symbol;
        String player2Symbol;
        Random random = new Random();
        if (random.nextBoolean()) {
            currentPlayer = senderId;
            player1Symbol = "X";
            player2Symbol = "O";            
        } else {
            currentPlayer = receiverId;
            player1Symbol = "O";
            player2Symbol = "X"; 
        }
        
        Game game = new Game(null, null, senderId, receiverId, board, currentPlayer, status, player1Symbol, player2Symbol, new Date());
        game = gameRepository.save(game);

        // create chatroom for game
        ChatRoom chatRoom = new ChatRoom(null, game.getGameId());
        chatRoomRepository.save(chatRoom);

        return game;
    }

    public Game makeMove(String gameId, int row, int col) {
        Game game = findGameById(gameId);

        if(game != null){

            if (game.getBoard().get(row).get(col).equals("-") && game.getStatus().equals("IN_PROGRESS")) {
                String currentPlayer = String.valueOf(game.getCurrentPlayer());
                
                // current player symbol
                String playerSymbol = currentPlayer.equals(game.getPlayer1Id()) ? game.getPlayer1Symbol() : game.getPlayer2Symbol(); 
                
                // update game board 
                game.getBoard().get(row).set(col, playerSymbol);
                if (checkWin(game)) {
                    game.setStatus(game.getCurrentPlayer() + " WINS");
                } else if (isBoardFull(game)) {
                    game.setStatus("DRAW");
                } else {
                    switchPlayer(game);
                }
            }
            
            // save game
            game = gameRepository.save(game);
        }
        // return game;
        return game;
    }

    private boolean checkWin(Game game) {
        List<List<String>> board = game.getBoard();
        String currentPlayer = String.valueOf(game.getCurrentPlayer());
        String playerSymbol = currentPlayer.equals(game.getPlayer1Id()) ? game.getPlayer1Symbol() : game.getPlayer2Symbol(); 

        for (int i = 0; i < 3; i++) {
            if ((board.get(i).get(0).equals(playerSymbol) && board.get(i).get(1).equals(playerSymbol) && board.get(i).get(2).equals(playerSymbol)) ||
                (board.get(0).get(i).equals(playerSymbol) && board.get(1).get(i).equals(playerSymbol) && board.get(2).get(i).equals(playerSymbol))) {
                return true;
            }
        }
        if ((board.get(0).get(0).equals(playerSymbol) && board.get(1).get(1).equals(playerSymbol) && board.get(2).get(2).equals(playerSymbol)) ||
            (board.get(0).get(2).equals(playerSymbol) && board.get(1).get(1).equals(playerSymbol) && board.get(2).get(0).equals(playerSymbol))) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull(Game game) {
        List<List<String>> board = game.getBoard();
        for (List<String> row : board) {
            for (String cell : row) {
                if (cell.equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }

    public Game updateGameOnPlayerLeaving(String gameId, String leavingPlayerId){
        Game game = findGameById(gameId);
        // game already won by user and he is trying to leave game, dont update anything
        if(game.getStatus().contains("WINS") || game.getStatus().contains("DRAW")) return null;
        if(game != null){
            // check if game is not already won
            if(!checkWin(game)){
                String winningPlayer = leavingPlayerId.equals(game.getPlayer1Id()) ? game.getPlayer2Id() : game.getPlayer1Id();
                game.setStatus(winningPlayer + " WINS (" + leavingPlayerId + " left the game)");
                game = gameRepository.save(game);
            }else{
                // game is won and user is trying to leave now
                return null;
            }
        }
        return game;
    }

    private void switchPlayer(Game game) {
        game.setCurrentPlayer(game.getCurrentPlayer().equals(game.getPlayer1Id()) ? game.getPlayer2Id() : game.getPlayer1Id());
    }

    }
