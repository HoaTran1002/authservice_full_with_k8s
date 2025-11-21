package com.backend.authservice.auth.controller;

import com.backend.authservice.user.dto.RegisterRequest;
import com.backend.authservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
        return ResponseEntity.accepted().body("Registration OK. Check email to verify.");
    }
}
