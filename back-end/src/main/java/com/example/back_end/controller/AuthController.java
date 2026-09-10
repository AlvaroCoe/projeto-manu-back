package com.example.back_end.controller;

import com.example.back_end.dto.ForgotPasswordRequestDTO;
import com.example.back_end.dto.LoginRequestDTO;
import com.example.back_end.dto.LoginResponseDTO;
import com.example.back_end.dto.MensagemDTO;
import com.example.back_end.dto.ResetPasswordRequestDTO;
import com.example.back_end.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    // Disponível para SOLICITANTE, N1, N2, N3 e ADMIN, pois todos ficam em tb_usuarios
    @PostMapping("/forgot-password")
    public ResponseEntity<MensagemDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.forgotPassword(dto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MensagemDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.resetPassword(dto));
    }
}