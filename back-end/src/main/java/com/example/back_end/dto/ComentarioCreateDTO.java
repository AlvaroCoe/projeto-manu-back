package com.example.back_end.dto;

import jakarta.validation.constraints.NotBlank;

public record ComentarioCreateDTO(

        @NotBlank(message = "A mensagem é obrigatória")
        String mensagem
) {
}