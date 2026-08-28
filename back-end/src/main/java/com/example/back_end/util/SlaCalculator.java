package com.example.back_end.util;

import com.example.back_end.entity.TicketEntity;
import com.example.back_end.enums.Prioridade;

import java.time.LocalDateTime;

public class SlaCalculator {

    private SlaCalculator() {
    }

    // Prazo de atendimento por prioridade, em horas
    public static int horasSla(Prioridade prioridade) {
        return switch (prioridade) {
            case ALTA -> 4;
            case MEDIA -> 24;
            case BAIXA -> 72;
        };
    }

    public static LocalDateTime calcularPrazo(TicketEntity entity) {
        return entity.getCreatedAt().plusHours(horasSla(entity.getPrioridade()));
    }

    // Se o chamado já foi encerrado, compara com o momento em que foi resolvido
    // (fica registrado pra sempre). Se ainda está aberto, compara com agora.
    public static boolean estaAtrasado(TicketEntity entity) {
        LocalDateTime prazo = calcularPrazo(entity);
        LocalDateTime referencia = entity.getResolvedAt() != null ? entity.getResolvedAt() : LocalDateTime.now();
        return referencia.isAfter(prazo);
    }
}