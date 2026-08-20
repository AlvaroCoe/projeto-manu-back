package com.example.back_end.dto;

import com.example.back_end.enums.UserRole;

public record LoginResponseDTO(
        Long id,
        String nome,
        String email,
        UserRole role,
        String token // Pode enviar "mock-token" por enquanto
) {
}