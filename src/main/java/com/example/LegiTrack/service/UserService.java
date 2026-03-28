package com.example.LegiTrack.service;

import com.example.LegiTrack.exception.DuplicateUserException;
import com.example.LegiTrack.exception.UserNotFoundException;
import com.example.LegiTrack.model.dto.request.UserRegistrationRequest;
import com.example.LegiTrack.model.entity.UserEntity;
import com.example.LegiTrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserEntity createUser(UserRegistrationRequest userRequest) {
        log.info("Checking for existing usernames and emails");
        if(userRepository.existsByUserName(userRequest.getUserName())) {
            throw new DuplicateUserException("Username already exists");
        } else if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateUserException("Email already in use");
        }

        log.info("Creating and saving user: {}", userRequest.getUserName());
        UserEntity userEntity = new UserEntity(
            userRequest.getEmail(),
            userRequest.getFirstName(),
            userRequest.getLastName(),
            userRequest.getUserName(),
            userRequest.getState()
        );
        UserEntity savedUser = userRepository.save(userEntity);
        return savedUser;
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
