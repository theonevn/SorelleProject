package com.example.sorelleapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.sorelleapp.data.UsuarioStorage;
import com.example.sorelleapp.data.api.dto.ErroResponse;
import com.example.sorelleapp.ui.login.LoginActivity;
import com.google.gson.Gson;

import retrofit2.Response;

public class ApiHelper {

    public static void voltarAoLogin(Activity activity, String mensagem) {
        UsuarioStorage.limparSessao(activity);
        if (mensagem != null) {
            Toast.makeText(activity, mensagem, Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static String lerErro(Response<?> resposta, String padrao) {
        try {
            if (resposta.errorBody() != null) {
                ErroResponse erro = new Gson().fromJson(
                        resposta.errorBody().string(), ErroResponse.class);
                if (erro != null && erro.getErro() != null) {
                    return erro.getErro();
                }
            }
        } catch (Exception ignorada) {
            // se não der pra ler a mensagem, usa a padrão
        }
        return padrao;
    }
}