package com.example.sorelleapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sorelleapp.data.api.dto.AuthResponse;

public class UsuarioStorage {

    private static final String PREFS = "sorelle_usuario";
    private static final String CHAVE_TOKEN = "token";
    private static final String CHAVE_ID = "usuario_id";
    private static final String CHAVE_NOME = "nome";
    private static final String CHAVE_EMAIL = "email";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void salvarSessao(Context context, AuthResponse resposta) {
        prefs(context).edit()
                .putString(CHAVE_TOKEN, resposta.getToken())
                .putInt(CHAVE_ID, resposta.getUsuarioId())
                .putString(CHAVE_NOME, resposta.getNome())
                .putString(CHAVE_EMAIL, resposta.getEmail())
                .apply();
    }

    public static String getToken(Context context) {
        return prefs(context).getString(CHAVE_TOKEN, null);
    }

    public static String getNome(Context context) {
        return prefs(context).getString(CHAVE_NOME, "");
    }

    public static boolean isLogado(Context context) {
        String token = getToken(context);
        return token != null && !token.isEmpty();
    }

    public static void limparSessao(Context context) {
        prefs(context).edit().clear().apply();
    }
}