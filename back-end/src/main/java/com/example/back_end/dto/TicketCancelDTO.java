package com.example.back_end.dto;

import jakarta.validation.constraints.NotBlank;

public record TicketCancelDTO(

        @NotBlank(message = "O motivo do cancelamento é obrigatório")
        String motivo
) {
}