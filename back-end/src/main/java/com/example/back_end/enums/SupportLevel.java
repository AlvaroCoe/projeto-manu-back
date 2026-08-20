package com.example.back_end.enums;

public enum SupportLevel {
    N1 ("n1"),
    N2 ("n2"),
    N3 ("n3");

    private final String texto;

    SupportLevel(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
