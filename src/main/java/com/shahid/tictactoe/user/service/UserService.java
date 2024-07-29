package com.shahid.tictactoe.user.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shahid.tictactoe.game.model.Game;
import com.shahid.tictactoe.game.repository.GameRepository;
import com.shahid.tictactoe.user.model.Status;
import com.shahid.tictactoe.user.model.User;
import com.shahid.tictactoe.user.model.UserStatsDTO;
import com.shahid.tictactoe.user.repository.UserRepository;
import com.shahid.tictactoe.user.util.PasswordUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public User authenticateOrRegisterUser(User user) {
        Optional<User> existingUserOpt = userRepository.findByNickName(user.getNickName());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (PasswordUtil.verifyPassword(user.getHashedPassword(), existingUser.getSalt(), existingUser.getHashedPassword())) {
                existingUser.setStatus(Status.ONLINE);
                existingUser.setLastOnline(new Date());
                userRepository.save(existingUser);
                return existingUser;
            } else {
                throw new RuntimeException("Password does not match");
            }
        } else {
            // Register a new user
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(user.getHashedPassword(), salt);
            user.setSalt(salt);
            user.setHashedPassword(hashedPassword);
            user.setStatus(Status.ONLINE);
            user.setJoinedOn(new Date());
            userRepository.save(user);
            return user;
        }
    }

    public void disconnect(User user){
        Optional<User> storedUserOpt = userRepository.findByNickName(user.getNickName());
        if (storedUserOpt.isPresent()) {
            User storedUser = storedUserOpt.get();
            storedUser.setStatus(Status.OFFLINE);
            storedUser.setLastOnline(new Date());
            userRepository.save(storedUser);
            System.out.println("user set to offline");
            System.out.println("after setting to offline" + user);
        }
    }

    public List<User> findConnectedUsers(){
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public List<UserStatsDTO> findAllTopUsers(){
        List<UserStatsDTO> topUsers = new ArrayList<>();

        // get all users
        List<User> users = userRepository.findAll();

        // get each user stats and add to userstats
        for(User user : users){
            topUsers.add(getPlayerStats(user.getNickName()));
        }

        // Sort the list based on wins in descending order
        Collections.sort(topUsers, new Comparator<UserStatsDTO>() {
            @Override
            public int compare(UserStatsDTO u1, UserStatsDTO u2) {
                int winComparison = Integer.compare(u2.getWins(), u1.getWins());
                if (winComparison != 0) {
                    return winComparison;
                }

                // If wins are equal, compare the ratio of games played to losses
                double ratio1 = (u1.getLosses() == 0) ? Double.POSITIVE_INFINITY : (double) u1.getGamesPlayed() / u1.getLosses();
                double ratio2 = (u2.getLosses() == 0) ? Double.POSITIVE_INFINITY : (double) u2.getGamesPlayed() / u2.getLosses();
                
                return Double.compare(ratio2, ratio1); // Higher ratio should come first
            }
        });

        return topUsers;
    }

    public UserStatsDTO getPlayerStats(String userId){
        UserStatsDTO userStatsDTO = new UserStatsDTO();
        List<Game> games = gameRepository.findByPlayer1IdOrPlayer2Id(userId, userId);
        Optional<User> userOpt = userRepository.findByNickName(userId);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            userStatsDTO.setNickname(userId);
            userStatsDTO.setFullName(user.getFullName());
            userStatsDTO.setStatus(String.valueOf(user.getStatus()));
            userStatsDTO.setJoinedOn(user.getJoinedOn());
            userStatsDTO.setLastOnline(user.getStatus() == Status.ONLINE ? new Date() : user.getLastOnline());

            // add played, wins, losses, draw
            int gamesPlayed = 0;
            int wins = 0;
            int losses = 0;
            int draws = 0;
            
            if(games.size() > 0){
                gamesPlayed = games.size();
                for (Game game : games) {
                    String status = game.getStatus();
                    if (status.contains(user.getNickName() + " WINS")) {
                        wins++;
                    } else if ("DRAW".equals(status)) {
                        draws++;
                    } else {
                        losses++;
                    }
                }
            }
            
            userStatsDTO.setGamesPlayed(gamesPlayed);
            userStatsDTO.setLosses(losses);
            userStatsDTO.setDraws(draws);
            userStatsDTO.setWins(wins);
        }
        return userStatsDTO;
    }
}
