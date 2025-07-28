package com.alura.literatura.model;

public enum Idioma {
    ESPANOL("es"),
    INGLES("en"),
    FRANCES("fr"),
    ALEMAN("de"),
    ITALIANO("it"),
    PORTUGUES("pt"),
    CHINO("zh"),
    JAPONES("ja"),
    RUSO("ru"),
    OTRO("otro");

    private String idiomaGutendex;

    Idioma(String idiomaGutendex) {
        this.idiomaGutendex = idiomaGutendex;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idiomaGutendex.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ningún idioma encontrado: " + text);
    }
}
