package com.shahid.tictactoe.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shahid.tictactoe.user.model.Status;
import com.shahid.tictactoe.user.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
    Optional<User> findByNickName(String nickName);
    List<User> findAllByStatus(Status status); // using query method on field name

}
