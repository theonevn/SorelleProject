package com.example.sorelleapp.data.api;

import com.example.sorelleapp.data.api.dto.AuthResponse;
import com.example.sorelleapp.data.api.dto.LoginRequest;
import com.example.sorelleapp.data.api.dto.RegistroRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/registrar")
    Call<AuthResponse> registrar(@Body RegistroRequest corpo);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest corpo);
}