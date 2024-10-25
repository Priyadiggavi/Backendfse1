package com.fse.shoppingapp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fse.shoppingapp.models.User;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    
    Optional<User> findByLoginId(String username);

    boolean existsByLoginId(String loginId);
}
