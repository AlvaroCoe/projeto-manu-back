package com.example.back_end.enums;

public enum UserRole {
    SOLICITANTE ("solicitante"),
    TECNICO_N1 ("n1"),
    TECNICO_N2 ("n2"),
    TECNICO_N3 ("n3");

    private final String texto;

    UserRole(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}