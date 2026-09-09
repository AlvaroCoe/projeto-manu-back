package com.example.back_end.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioStatusUpdateDTO(

        @NotNull(message = "O campo ativo é obrigatório")
        Boolean ativo
) {
}