package com.example.back_end.dto;

import jakarta.validation.constraints.NotBlank;

public record TicketEscalateDTO(

        @NotBlank(message = "O motivo do escalonamento é obrigatório")
        String motivo,

        Long technicianId
) {
}
