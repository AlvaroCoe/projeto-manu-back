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
        LocalDateTime prazoLimite, // NOVO: prazo calculado a partir da prioridade
        boolean atrasado,          // NOVO: true se passou do prazo e ainda não foi finalizado
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
                calcularPrazoLimite(entity),   // NOVO
                calcularAtrasado(entity),      // NOVO
                entity.getClient() != null ? new UsuarioResponseDTO(entity.getClient()) : null,
                entity.getTechnician() != null ? new UsuarioResponseDTO(entity.getTechnician()) : null,
                entity.getEquipamento() != null ? new EquipamentoResponseDTO(entity.getEquipamento()) : null
        );
    }

    // Métodos estáticos "auxiliares": um record não pode ter lógica solta dentro do
    // construtor como uma classe comum, então isolamos o cálculo aqui e só chamamos
    // eles lá em cima, dentro do this(...).
    private static LocalDateTime calcularPrazoLimite(TicketEntity entity) {
        return entity.getCreatedAt().plusHours(horasPorPrioridade(entity.getPrioridade()));
    }

    private static boolean calcularAtrasado(TicketEntity entity) {
        LocalDateTime prazoLimite = calcularPrazoLimite(entity);
        return LocalDateTime.now().isAfter(prazoLimite)
                && entity.getStatus() != TicketStatus.FINALIZADO;
    }

    private static long horasPorPrioridade(Prioridade prioridade) {
        return switch (prioridade) {
            case ALTA -> 4;
            case MEDIA -> 24;
            case BAIXA -> 72;
        };
    }
}