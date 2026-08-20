package com.example.back_end.dto;

import com.example.back_end.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCreateDTO(

        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "A prioridade é obrigatória")
        Prioridade prioridade,

        String imageUrl,

        @NotNull(message = "O solicitante (cliente) é obrigatório")
        Long clientId,

        @NotNull(message = "O equipamento é obrigatório")
        Long equipamentoId
) {
}
