package com.example.back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipamentoCreateDTO(

        @NotBlank(message = "O nome do equipamento é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O código do patrimônio é obrigatório")
        String codigoPatrimonio,

        @NotBlank(message = "O setor é obrigatório")
        String setor
) {
}
