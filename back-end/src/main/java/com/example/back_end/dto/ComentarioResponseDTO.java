package com.example.back_end.dto;

import com.example.back_end.entity.ComentarioEntity;
import java.time.LocalDateTime;

public record ComentarioResponseDTO(
        Long id,
        String mensagem,
        LocalDateTime createdAt,
        UsuarioResponseDTO autor
) {
    public ComentarioResponseDTO(ComentarioEntity entity) {
        this(
                entity.getId(),
                entity.getMensagem(),
                entity.getCreatedAt(),
                new UsuarioResponseDTO(entity.getAutor())
        );
    }
}