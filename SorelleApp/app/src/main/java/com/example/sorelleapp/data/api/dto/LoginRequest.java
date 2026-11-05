package com.example.sorelleapp.data.api.dto;

public class LoginRequest {
    private final String email;
    private final String senha;

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}