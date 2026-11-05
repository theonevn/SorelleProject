package com.example.sorelleapp.data.api.dto;

public class RegistroRequest {
    private final String nome;
    private final String email;
    private final String senha;

    public RegistroRequest(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
}