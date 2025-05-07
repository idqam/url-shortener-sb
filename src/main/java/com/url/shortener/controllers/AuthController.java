package com.url.shortener.controllers;

import com.url.shortener.dtos.LoginRequest;
import com.url.shortener.dtos.RegisterRequest;
import com.url.shortener.models.User;
import com.url.shortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor

public class AuthController {

    private final UserService userService;




    @PostMapping("/public/register")
    public ResponseEntity<?> loginUser (@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userService.authUser(loginRequest));
    }

    @PostMapping("/public/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userService.existsByUserName(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("userName is taken");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already registered");
        }

        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setRole("ROLE_USER");

        userService.registerUser(user);

        return ResponseEntity.ok("✅ User registered successfully!");
    }

    @GetMapping("/public/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("pong");
    }
}
