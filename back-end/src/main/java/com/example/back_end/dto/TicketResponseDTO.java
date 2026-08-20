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
                entity.getClient() != null ? new UsuarioResponseDTO(entity.getClient()) : null,
                entity.getTechnician() != null ? new UsuarioResponseDTO(entity.getTechnician()) : null,
                entity.getEquipamento() != null ? new EquipamentoResponseDTO(entity.getEquipamento()) : null
        );
    }
}
