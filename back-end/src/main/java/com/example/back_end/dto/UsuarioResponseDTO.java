package com.example.back_end.dto;

import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.enums.UserRole;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        UserRole role
) {
    public UsuarioResponseDTO(UsuarioEntity entity) {
        this(entity.getId(), entity.getNome(), entity.getEmail(), entity.getRole());
    }
}
