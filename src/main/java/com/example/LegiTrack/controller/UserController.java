package com.example.LegiTrack.controller;

import com.example.LegiTrack.model.dto.request.UserRegistrationRequest;
import com.example.LegiTrack.model.dto.response.UserResponse;
import com.example.LegiTrack.model.entity.UserEntity;
import com.example.LegiTrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserEntity userEntity = userService.createUser(userRegistrationRequest);
        UserResponse userResponse = UserResponse.from(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserEntity userEntity = userService.getUserById(id);
        UserResponse userResponse = UserResponse.from(userEntity);
        return ResponseEntity.ok(userResponse);
    }
}
