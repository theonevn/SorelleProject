package com.example.sorelleapp.data.api.dto;

public class AuthResponse {
    private String token;
    private int usuarioId;
    private String nome;
    private String email;

    public String getToken() { return token; }
    public int getUsuarioId() { return usuarioId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}