package com.shahid.tictactoe.user.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.shahid.tictactoe.user.model.User;

@Service
public class MatchMakingService {

    private Queue<User> waitingUsers = new LinkedList<>();

    public User matchPlayer(User user) {
        if (waitingUsers.isEmpty()) {
            waitingUsers.add(user);
            return null;
        } else {
            User opponent = waitingUsers.poll();
            return opponent;
        }
    }

    public User removePlayerFromMatchQueue(String userId){
        Iterator<User> iterator = waitingUsers.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getNickName().equals(userId)) {
                iterator.remove();
                System.out.println("removed user from queue: " + userId);
                return user;
            }
        }
        System.out.println("user not found in queue: " + userId);
        return null;
    }
}