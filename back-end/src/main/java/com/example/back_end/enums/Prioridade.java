package com.example.back_end.enums;

public enum Prioridade {
    BAIXA ("baixa"),
    MEDIA ("media"),
    ALTA ("alta");

    private final String texto;

    Prioridade(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
