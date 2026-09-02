package com.example.back_end.dto;

import com.example.back_end.entity.TicketEntity;
import com.example.back_end.enums.Prioridade;
import com.example.back_end.enums.SupportLevel;
import com.example.back_end.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponseDTO(
        Long id,
        String titulo,
        String descricao,
        Prioridade prioridade,
        TicketStatus status,
        SupportLevel currentLevel,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime prazoLimite,
        boolean atrasado,
        UsuarioResponseDTO client,
        UsuarioResponseDTO technician,
        EquipamentoResponseDTO equipamento
) {
    public TicketResponseDTO(TicketEntity entity) {
        this(
                entity.getId(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getPrioridade(),
                entity.getStatus(),
                entity.getCurrentLevel(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                calcularPrazoLimite(entity),
                calcularAtrasado(entity),
                entity.getClient() != null ? new UsuarioResponseDTO(entity.getClient()) : null,
                entity.getTechnician() != null ? new UsuarioResponseDTO(entity.getTechnician()) : null,
                entity.getEquipamento() != null ? new EquipamentoResponseDTO(entity.getEquipamento()) : null
        );
    }

    private static LocalDateTime calcularPrazoLimite(TicketEntity entity) {
        return entity.getCreatedAt().plusHours(horasPorPrioridade(entity.getPrioridade()));
    }

    // Enquanto o chamado está aberto, compara com "agora" (atraso em tempo real).
    // Depois de resolvido (FINALIZADO ou CANCELADO), compara com o momento exato
    // em que foi resolvido — assim um chamado que atrasou continua marcado como
    // atrasado pra sempre, mesmo depois de fechado.
    private static boolean calcularAtrasado(TicketEntity entity) {
        LocalDateTime prazoLimite = calcularPrazoLimite(entity);
        LocalDateTime referencia = entity.getResolvedAt() != null ? entity.getResolvedAt() : LocalDateTime.now();
        return referencia.isAfter(prazoLimite);
    }

    private static long horasPorPrioridade(Prioridade prioridade) {
        return switch (prioridade) {
            case ALTA -> 4;
            case MEDIA -> 24;
            case BAIXA -> 72;
        };
    }
}