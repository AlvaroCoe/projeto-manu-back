package com.example.back_end.dto;

import com.example.back_end.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TicketStatusUpdateDTO(

        @NotNull(message = "O status é obrigatório")
        TicketStatus status
) {
}
