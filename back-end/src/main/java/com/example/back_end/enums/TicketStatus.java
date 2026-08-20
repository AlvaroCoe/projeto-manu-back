package com.example.back_end.enums;

public enum TicketStatus {
    ABERTO ("aberto"),
    ANDAMENTO ("andamento"),
    FINALIZADO ("finalizado"),
    CANCELADO ("cancelado");

    private final String texto;

    TicketStatus(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}