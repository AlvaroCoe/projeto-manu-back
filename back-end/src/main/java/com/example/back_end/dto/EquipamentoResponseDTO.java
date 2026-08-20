package com.example.back_end.dto;

import com.example.back_end.entity.EquipamentoEntity;

public record EquipamentoResponseDTO(
        Long id,
        String nome,
        String codigoPatrimonio,
        String setor
) {
        public EquipamentoResponseDTO(EquipamentoEntity entity) {
                this(entity.getId(), entity.getNome(), entity.getCodigoPatrimonio(), entity.getSetor());
        }
}
