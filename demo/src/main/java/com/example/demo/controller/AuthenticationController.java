package com.example.demo.controller;

import com.example.demo.dto.AuthenticationRequestDTO;
import com.example.demo.dto.AuthenticationResponseDTO;
import com.example.demo.dto.RefreshTokenRequestDTO;
import com.example.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
