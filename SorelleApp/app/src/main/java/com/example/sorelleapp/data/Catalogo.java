package com.example.sorelleapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sorelleapp.R;
import com.example.sorelleapp.data.api.ApiClient;
import com.example.sorelleapp.data.api.dto.ProdutoResponse;
import com.example.sorelleapp.model.Produto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Catalogo {

    public interface Ouvinte {
        void aoTerminar(boolean sucesso);
    }

    private static final String PREFS = "sorelle_catalogo";
    private static final String CHAVE_JSON = "produtos_json";
    private static final Type TIPO_LISTA = new TypeToken<List<ProdutoResponse>>() { }.getType();

    private static final List<Produto> PRODUTOS = new ArrayList<>();

    static {
        carregarPadrao();
    }

    public static List<Produto> getProdutos() {
        return PRODUTOS;
    }

    public static void carregarDoCache(Context context) {
        String json = prefs(context).getString(CHAVE_JSON, null);
        if (json == null) return;
        try {
            List<ProdutoResponse> lista = new Gson().fromJson(json, TIPO_LISTA);
            if (lista != null && !lista.isEmpty()) {
                aplicar(lista);
            }
        } catch (Exception ignorada) {
        }
    }

    public static void sincronizar(Context context, Ouvinte ouvinte) {
        Context app = context.getApplicationContext();

        ApiClient.get().produtos().enqueue(new Callback<List<ProdutoResponse>>() {
            @Override
            public void onResponse(Call<List<ProdutoResponse>> call,
                                   Response<List<ProdutoResponse>> resposta) {
                List<ProdutoResponse> lista = resposta.body();
                if (resposta.isSuccessful() && lista != null && !lista.isEmpty()) {
                    aplicar(lista);
                    prefs(app).edit()
                            .putString(CHAVE_JSON, new Gson().toJson(lista, TIPO_LISTA))
                            .apply();
                    ouvinte.aoTerminar(true);
                } else {
                    ouvinte.aoTerminar(false);
                }
            }

            @Override
            public void onFailure(Call<List<ProdutoResponse>> call, Throwable t) {
                ouvinte.aoTerminar(false);
            }
        });
    }

    private static void aplicar(List<ProdutoResponse> lista) {
        PRODUTOS.clear();
        for (ProdutoResponse p : lista) {
            PRODUTOS.add(new Produto(
                    p.getId(),
                    p.getNome(),
                    p.getDescricao(),
                    p.getPreco(),
                    p.getTag() == null ? "" : p.getTag(),
                    p.getArvores(),
                    ImagensProduto.paraNome(p.getNome())));
        }
    }

    private static void carregarPadrao() {
        PRODUTOS.clear();
        PRODUTOS.add(new Produto(1, "Sérum Açaí",
                "Bioativos puros da floresta.", 89.90, "Vegano", 1,
                R.drawable.serum_acai));
        PRODUTOS.add(new Produto(2, "Creme Castanha",
                "Hidratação intensa e prolongada.", 55.90, "Reciclado", 3,
                R.drawable.creme_castanha));
        PRODUTOS.add(new Produto(3, "Óleo Andiroba",
                "Regenerador natural da pele.", 120.00, "Comunidade", 1,
                R.drawable.oleo_andiroba));
        PRODUTOS.add(new Produto(4, "Kit Ritual da Mata",
                "Experiência de spa sustentável.", 210.50, "Impacto Triplo", 3,
                R.drawable.kit_ritual));
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}